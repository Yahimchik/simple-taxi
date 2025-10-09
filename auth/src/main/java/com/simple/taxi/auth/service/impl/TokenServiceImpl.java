package com.simple.taxi.auth.service.impl;

import com.simple.taxi.auth.exception.InvalidRefreshTokenException;
import com.simple.taxi.auth.exception.NotFoundException;
import com.simple.taxi.auth.mapper.RefreshTokenMapper;
import com.simple.taxi.auth.model.dto.RefreshTokenDTO;
import com.simple.taxi.auth.model.dto.RefreshTokenRequest;
import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.model.entity.RefreshToken;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.UserDevice;
import com.simple.taxi.auth.repository.RefreshTokenRepository;
import com.simple.taxi.auth.repository.UserDeviceRepository;
import com.simple.taxi.auth.repository.UserRepository;
import com.simple.taxi.auth.security.JwtProvider;
import com.simple.taxi.auth.service.TokenService;
import com.simple.taxi.auth.util.IpUtils;
import com.simple.taxi.auth.util.RefreshTokenManager;
import com.simple.taxi.auth.util.UuidGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.simple.taxi.auth.model.enums.ErrorType.*;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private static final long REFRESH_TOKEN_TTL = 7 * 24 * 3600;

    private final JwtProvider jwtProvider;
    private final UserDeviceRepository userDeviceRepository;
    private final UuidGenerator uuidGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenManager tokenManager;
    private final UserRepository userRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public RefreshToken createRefreshToken(User user, UserDevice userDevice) {
        return refreshTokenRepository
                .findByUserDeviceAndRevokedFalse(userDevice)
                .flatMap(token -> {
                    if (tokenManager.isValid(token)) {
                        tokenManager.extendExpiration(token, REFRESH_TOKEN_TTL);
                        refreshTokenRepository.save(token);
                        return Optional.of(token);
                    }
                    tokenManager.revoke(token);
                    refreshTokenRepository.save(token);
                    return Optional.of(token);
                })
                .orElseGet(() -> saveNewRefreshToken(user, userDevice));
    }

    @Override
    public UUID validateRefreshToken(String token) {
        RefreshToken refreshToken = findRefreshToken(token);
        tokenManager.assertValid(refreshToken);
        return refreshToken.getUserDevice().getUser().getId();
    }

    @Override
    @Transactional
    public void revokeTokenByDevice(UUID userId, String deviceId) {
        UserDevice userDevice = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new NotFoundException(USER_DEVICE_NOT_FOUND));

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserDeviceAndRevokedFalse(userDevice);
        tokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    @Override
    @Transactional
    public void revokeAllTokens(UUID userId) {
        List<UserDevice> userDevices = userDeviceRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(USER_DEVICE_NOT_FOUND));

        for (UserDevice userDevice : userDevices) {
            List<RefreshToken> tokens = refreshTokenRepository.findAllByUserDeviceAndRevokedFalse(userDevice);
            tokens.forEach(token -> token.setRevoked(true));
            refreshTokenRepository.saveAll(tokens);
        }
    }

    @Override
    public List<RefreshTokenDTO> getActiveTokens(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        List<UserDevice> devices = userDeviceRepository.findUserDeviceByUser(user);
        List<RefreshToken> tokens = new ArrayList<>();

        for (UserDevice userDevice : devices) {
            tokens.add(refreshTokenRepository.findByUserDeviceAndRevokedFalse(userDevice)
                    .orElseThrow(() -> new NotFoundException(TOKEN_NOT_FOUND)));
        }

        return tokens.stream()
                .map(refreshTokenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkToken(String token) {
        return jwtProvider.validateToken(token, true);
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request, String deviceInfo) {
        UUID userId = validateRefreshToken(request.refreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        var userDevice = getOrCreateUserDevice(user, request.deviceId(), deviceInfo);

        return generateTokens(user, userDevice);
    }

    @Override
    public TokenResponse generateTokens(User user, UserDevice userDevice) {
        String accessToken = null;
        String refreshTokenStr = null;
        Instant expiresAt = null;

        if (user != null) {
            accessToken = jwtProvider.generateAccessToken(user.getId(),
                    user.getRoles().stream().findFirst().toString());
            RefreshToken refreshToken = createRefreshToken(user, userDevice);
            refreshTokenStr = refreshToken.getToken();
            expiresAt = refreshToken.getExpiresAt();
        }

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .deviceId(userDevice.getDeviceId())
                .deviceInfo(userDevice.getDeviceInfo())
                .expiresIn(expiresAt)
                .build();
    }

    @Override
    public UserDevice getOrCreateUserDevice(User user, String deviceId, String deviceInfo) {

        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = uuidGenerator.generateV7().toString();
        }

        String finalDeviceId = deviceId;

        return user.getUserDevices().stream()
                .filter(d -> d.getDeviceId().equals(finalDeviceId))
                .findFirst()
                .map(existing -> {
                    existing.setDeviceInfo(deviceInfo);
                    existing.setLastLogin(Instant.now());
                    existing.setIpAddress(getUserIp());
                    return userDeviceRepository.save(existing);
                })
                .orElseGet(() -> {
                    UserDevice newDevice = UserDevice.builder()
                            .user(user)
                            .deviceId(finalDeviceId)
                            .deviceInfo(deviceInfo)
                            .lastLogin(Instant.now())
                            .ipAddress(getUserIp())
                            .build();
                    UserDevice saved = userDeviceRepository.save(newDevice);
                    user.getUserDevices().add(saved);
                    return saved;
                });
    }

    @Override
    public String getUserIp() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        return IpUtils.getClientIp(request);
    }

    private RefreshToken findRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException(REFRESH_TOKEN_INVALID));
    }

    private RefreshToken saveNewRefreshToken(User user, UserDevice userDevice) {
        RefreshToken refreshToken = tokenManager.create(
                userDevice,
                jwtProvider.generateRefreshToken(user.getId()),
                uuidGenerator.generateV7(),
                REFRESH_TOKEN_TTL
        );
        return refreshTokenRepository.save(refreshToken);
    }
}