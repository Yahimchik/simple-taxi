package com.simple.taxi.auth.service.impl;

import com.simple.taxi.auth.exception.NotFoundException;
import com.simple.taxi.auth.kafka.EventProducer;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.VerificationToken;
import com.simple.taxi.auth.model.enums.Type;
import com.simple.taxi.auth.repository.UserRepository;
import com.simple.taxi.auth.repository.VerificationTokenRepository;
import com.simple.taxi.auth.service.NotificationService;
import com.simple.taxi.auth.service.VerificationTokenService;
import com.simple.taxi.auth.util.VerificationTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.simple.taxi.auth.model.enums.ErrorType.USER_NOT_FOUND;
import static com.simple.taxi.auth.model.enums.ErrorType.VERIFICATION_TOKEN_NOT_FOUND;
import static com.simple.taxi.auth.model.enums.Status.ACTIVE;
import static com.simple.taxi.dto.NotificationType.RESEND_CONFIRMATION;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final VerificationTokenManager verificationTokenManager;
    private final EventProducer eventProducer;
    private final NotificationService notificationService;

    @Override
    public VerificationToken createVerificationToken(User user) {
        verificationTokenRepository.deleteByUserId(user.getId());
        var token = verificationTokenManager.createVerificationToken(user, Type.EMAIL, false);
        return verificationTokenRepository.save(token);
    }

    @Override
    public boolean verifyToken(String token) {
        var verificationToken = findVerificationTokenByTokenValue(token);
        verificationTokenManager.isTokenExpired(verificationToken);

        User user = verificationToken.getUser();
        user.setStatus(ACTIVE);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        return true;
    }

    @Override
    public void resendVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        verificationTokenManager.isActive(user);

        verificationTokenRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .ifPresent(token -> {
                    verificationTokenManager.requestedToFrequently(token);
                    verificationTokenRepository.delete(token);
                });

        var token = createVerificationToken(user);
        notificationService.sendEmail(user.getId(), user.getEmail(), RESEND_CONFIRMATION, Map.of("token", token.getToken()));
    }

    @Override
    public VerificationToken findVerificationTokenByTokenValue(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException(VERIFICATION_TOKEN_NOT_FOUND));
    }
}