package com.simple.taxi.auth.model.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserDeviceDTO(
        Long id,
        String deviceId,
        String deviceInfo,
        String ipAddress,
        Instant lastLogin
) {
}