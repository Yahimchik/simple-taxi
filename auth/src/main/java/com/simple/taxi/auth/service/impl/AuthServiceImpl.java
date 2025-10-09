package com.simple.taxi.auth.service.impl;

import com.simple.taxi.auth.exception.AuthException;
import com.simple.taxi.auth.exception.CaptchaRequiredException;
import com.simple.taxi.auth.exception.TwoFactorRequiredException;
import com.simple.taxi.auth.exception.ValidationException;
import com.simple.taxi.auth.model.dto.AuthRequest;
import com.simple.taxi.auth.model.dto.LoginContext;
import com.simple.taxi.auth.model.dto.LoginRequest;
import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.enums.LoginType;
import com.simple.taxi.auth.repository.UserRepository;
import com.simple.taxi.auth.service.AuthService;
import com.simple.taxi.auth.service.LoginAttemptService;
import com.simple.taxi.auth.service.TokenService;
import com.simple.taxi.auth.service.social.SocialAuthProvider;
import com.simple.taxi.auth.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static com.simple.taxi.auth.model.enums.ErrorType.*;
import static com.simple.taxi.auth.model.enums.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordValidator passwordValidator;

    private final TokenService tokenService;
    private final LoginAttemptService attemptService;

    private final Map<LoginType, SocialAuthProvider> socialAuthProviders;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request, String clientIp, String deviceInfo) {
        LoginContext ctx = LoginContext.fromRequest(request, clientIp, deviceInfo);
        User user = findByEmailOrPhone(ctx);
        return performLogin(ctx, user, () -> validatePassword(request.password(), user.getPasswordHash(), ctx));
    }

    @Override
    @Transactional
    public TokenResponse loginWithSocial(AuthRequest request, LoginType loginType, String clientIp, String deviceInfo) {
        SocialAuthProvider provider = findSocialProvider(loginType);
        User user = provider.authenticate(request);
        LoginContext ctx = LoginContext.fromUser(user, request.deviceId(), clientIp, deviceInfo);
        return performLogin(ctx, user, null); // Пароль не проверяется
    }

    @Override
    @Transactional
    public void logout(UUID userId, String deviceId) {
        tokenService.revokeTokenByDevice(userId, deviceId);
    }

    @Override
    @Transactional
    public void logoutAll(UUID userId) {
        tokenService.revokeAllTokens(userId);
    }

    @Override
    public String getClientId(LoginType loginType) {
        return findSocialProvider(loginType).getOAuthClient().getClientId();
    }

    private TokenResponse performLogin(LoginContext ctx, User user, ThrowingRunnable passwordCheck) {
        checkBlocked(ctx);
        checkUserActivation(user);

        var userDevice = tokenService.getOrCreateUserDevice(user, ctx.deviceId(), ctx.deviceInfo());
        if (passwordCheck != null) passwordCheck.run();

        attemptService.loginSucceeded(ctx);
        checkTwoFactor(user);

        return tokenService.generateTokens(user, userDevice);
    }

    private void checkBlocked(LoginContext ctx) {
        if (attemptService.isBlocked(ctx)) throw new AuthException(USER_BLOCKED);
    }

    private void checkUserActivation(User user) {
        if (user.getStatus() != ACTIVE) throw new AuthException(USER_NOT_ACTIVATED);
    }

    private void validatePassword(String rawPassword, String passwordHash, LoginContext ctx) {
        try {
            passwordValidator.validatePassword(rawPassword, passwordHash, INVALID_CREDENTIALS);
        } catch (ValidationException e) {
            throw processFailedLoginAndThrow(ctx);
        }
    }

    private AuthException processFailedLoginAndThrow(LoginContext ctx) {
        attemptService.loginFailed(ctx.identifier(), ctx.deviceId(), ctx.deviceInfo(), ctx.clientIp());
        if (attemptService.isCaptchaRequired(ctx)) throw new CaptchaRequiredException(CAPTCHA_REQUIRED);
        return new AuthException(INVALID_CREDENTIALS);
    }

    private void checkTwoFactor(User user) {
        if (!user.isTwoFactorEnabled()) return;
        throw new TwoFactorRequiredException(TWO_FACTOR_REQUIRED, Map.of("email", user.getEmail()));
    }

    private SocialAuthProvider findSocialProvider(LoginType loginType) {
        var provider = socialAuthProviders.get(loginType);
        if (provider == null) throw new AuthException(UNSUPPORTED_LOGIN_TYPE);
        return provider;
    }

    private User findByEmailOrPhone(LoginContext ctx) {
        return userRepository.findByEmailOrPhone(ctx.identifier())
                .orElseThrow(() -> processFailedLoginAndThrow(ctx));
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws AuthException;
    }
}