package com.simple.taxi.auth.util;

import com.simple.taxi.auth.exception.ValidationException;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.VerificationToken;
import com.simple.taxi.auth.model.enums.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

import static com.simple.taxi.auth.model.enums.ErrorType.*;
import static com.simple.taxi.auth.model.enums.Status.ACTIVE;

@Component
@RequiredArgsConstructor
public class VerificationTokenManager {

    private static final long RESEND_INTERVAL = 60;
    private final UuidGenerator uuidGenerator;

    public void isTokenExpired(VerificationToken token) {
        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ValidationException(VERIFICATION_TOKEN_EXPIRED);
        }
    }

    public void requestedToFrequently(VerificationToken token) {
        if (Instant.now().isBefore(token.getCreatedAt().plusSeconds(RESEND_INTERVAL))) {
            throw new ValidationException(VERIFICATION_TOKEN_TOO_FREQUENT);
        }
    }

    public void isActive(User user) {
        if (user.getStatus() == ACTIVE)
            throw new ValidationException(USER_ALREADY_ACTIVE);
    }

    public VerificationToken createVerificationToken(User user, Type type, boolean used) {
        return VerificationToken.builder()
                .id(uuidGenerator.generateV7())
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusSeconds(15 * 60))
                .type(type)
                .used(used)
                .build();
    }
}