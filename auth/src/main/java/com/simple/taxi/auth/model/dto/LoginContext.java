package com.simple.taxi.auth.model.dto;

import com.simple.taxi.auth.model.entity.User;

public record LoginContext(
        String identifier,
        String deviceId,
        String clientIp,
        String deviceInfo
) {
    public static LoginContext fromRequest(LoginRequest request, String clientIp, String deviceInfo) {
        return new LoginContext(
                request.emailOrPhone(),
                request.deviceId(),
                clientIp,
                deviceInfo
        );
    }

    public static LoginContext fromUser(User user, String deviceId, String clientIp, String deviceInfo) {
        return new LoginContext(
                user.getEmail(),
                deviceId,
                clientIp,
                deviceInfo
        );
    }
}