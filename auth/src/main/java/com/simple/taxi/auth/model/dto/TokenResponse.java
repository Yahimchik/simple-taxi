package com.simple.taxi.auth.model.dto;

import lombok.*;

import java.time.Instant;

@Builder
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String deviceId,
        String deviceInfo,
        Instant expiresIn
) {
}