package com.simple.taxi.auth.model.dto;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenDTO(
        UUID id,
        UserDeviceDTO userDevice,
        String token,
        Instant expiresAt,
        boolean revoked,
        Instant createdAt
) {
}