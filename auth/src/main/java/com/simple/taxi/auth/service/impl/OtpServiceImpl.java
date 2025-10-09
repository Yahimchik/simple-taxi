package com.simple.taxi.auth.service.impl;

import com.simple.taxi.auth.exception.NotFoundException;
import com.simple.taxi.auth.exception.ValidationException;
import com.simple.taxi.auth.kafka.EventProducer;
import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.UserOtp;
import com.simple.taxi.auth.repository.UserOtpRepository;
import com.simple.taxi.auth.repository.UserRepository;
import com.simple.taxi.auth.service.NotificationService;
import com.simple.taxi.auth.service.OtpService;
import com.simple.taxi.auth.service.TokenService;
import com.simple.taxi.dto.NotificationChannel;
import com.simple.taxi.dto.NotificationEvent;
import com.simple.taxi.dto.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static com.simple.taxi.auth.model.enums.ErrorType.TOKEN_NOT_FOUND;
import static com.simple.taxi.auth.model.enums.ErrorType.USER_NOT_FOUND;
import static com.simple.taxi.dto.NotificationType.TWO_FACTOR_AUTHENTICATION_CONFIRMATION;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final UserOtpRepository otpRepository;
    private final SecureRandom random;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EventProducer eventProducer;
    private final NotificationService notificationService;

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public UserOtp generateOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        String code = String.format("%06d", random.nextInt(1_000_000));

        UserOtp otp = UserOtp.builder()
                .user(user)
                .code(code)
                .expiresAt(Instant.now().plus(5, MINUTES))
                .used(false)
                .build();

        otpRepository.save(otp);
        notificationService.sendEmail(user.getId(), user.getEmail(), TWO_FACTOR_AUTHENTICATION_CONFIRMATION, Map.of("code", code));

        return otp;
    }

    @Override
    @Transactional
    public TokenResponse verifyOtpAndLogin(String email, String code, String deviceId, String deviceInfo) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        return otpRepository.findByUserIdAndCodeAndUsedFalse(user.getId(), code)
                .filter(otp -> otp.getExpiresAt().isAfter(Instant.now()))
                .map(otp -> {
                    otp.setUsed(true);
                    var userDevice = tokenService.getOrCreateUserDevice(user, deviceId, deviceInfo);
                    otpRepository.save(otp);
                    return tokenService.generateTokens(user, userDevice);
                })
                .orElseThrow(() -> new ValidationException(TOKEN_NOT_FOUND));

    }

    @Override
    @Transactional
    public void enable2fa(UUID id) {
        setTwoFactorState(id, true);
    }

    @Override
    @Transactional
    public void disable2fa(UUID id) {
        setTwoFactorState(id, false);
    }

    private void setTwoFactorState(UUID id, boolean twoFactorEnabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        user.setTwoFactorEnabled(twoFactorEnabled);
        userRepository.save(user);
    }
}