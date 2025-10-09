package com.simple.taxi.auth.util;

import com.simple.taxi.auth.exception.InvalidRefreshTokenException;
import com.simple.taxi.auth.exception.RefreshTokenExpiredException;
import com.simple.taxi.auth.model.entity.RefreshToken;
import com.simple.taxi.auth.model.entity.UserDevice;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

import static com.simple.taxi.auth.model.enums.ErrorType.REFRESH_TOKEN_EXPIRED;
import static com.simple.taxi.auth.model.enums.ErrorType.REFRESH_TOKEN_INVALID;

@Component
public class RefreshTokenManager {

    public boolean isValid(RefreshToken token) {
        return !token.isRevoked() && token.getExpiresAt().isAfter(Instant.now());
    }

    public void assertValid(RefreshToken token) {
        if (token.isRevoked()) {
            throw new InvalidRefreshTokenException(REFRESH_TOKEN_INVALID);
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenExpiredException(REFRESH_TOKEN_EXPIRED);
        }
    }

    public void extendExpiration(RefreshToken token, long ttlSeconds) {
        token.setExpiresAt(Instant.now().plusSeconds(ttlSeconds));
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
    }

    public RefreshToken create(UserDevice user, String rawToken, UUID id, long ttlSeconds) {
        return RefreshToken.builder()
                .id(id)
                .userDevice(user)
                .token(rawToken)
                .expiresAt(Instant.now().plusSeconds(ttlSeconds))
                .revoked(false)
                .build();
    }
}