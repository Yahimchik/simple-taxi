package com.simple.taxi.auth.service;

import com.simple.taxi.auth.model.dto.AuthRequest;
import com.simple.taxi.auth.model.dto.LoginRequest;
import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.model.enums.LoginType;

import java.util.UUID;

public interface AuthService {

    TokenResponse login(LoginRequest request, String clientIp, String deviceInfo);

    TokenResponse loginWithSocial(AuthRequest request, LoginType loginType, String clientIp, String deviceInfo);

    void logout(UUID userId, String refreshToken);

    void logoutAll(UUID userId);

    String getClientId(LoginType loginType);
}