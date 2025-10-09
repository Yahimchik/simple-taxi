package com.simple.taxi.auth.model.dto;

public record AuthRequest(
        String code,
        String redirectUri,
        String deviceId
) {}