package com.simple.taxi.auth.model.dto;

import lombok.Builder;

@Builder
public record RefreshTokenRequest(
        String refreshToken,
        String deviceId
) {
}