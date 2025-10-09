package com.simple.taxi.auth.service;

import com.simple.taxi.auth.model.dto.RefreshTokenDTO;
import com.simple.taxi.auth.model.dto.RefreshTokenRequest;
import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.model.entity.RefreshToken;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.UserDevice;

import java.util.List;
import java.util.UUID;

public interface TokenService {

    RefreshToken createRefreshToken(User user, UserDevice userDevice);

    UUID validateRefreshToken(String token);

    void revokeAllTokens(UUID userId);

    void revokeTokenByDevice(UUID userId, String refreshToken);

    List<RefreshTokenDTO> getActiveTokens(UUID userId);

    boolean checkToken(String token);

    TokenResponse refreshToken(RefreshTokenRequest request,String deviceInfo);

    TokenResponse generateTokens(User user, UserDevice userDevice);

    UserDevice getOrCreateUserDevice(User user, String deviceId, String deviceInfo);

    String getUserIp();
}