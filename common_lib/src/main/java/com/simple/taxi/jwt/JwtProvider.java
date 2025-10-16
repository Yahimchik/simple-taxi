package com.simple.taxi.jwt;

import java.util.UUID;

public interface JwtProvider {

    String generateAccessToken(UUID userId, String role);

    String generateRefreshToken(UUID userId);

    UUID getUserIdFromToken(String token, boolean isAccessToken);

    String getRoleFromAccessToken(String token);

    boolean validateToken(String token, boolean isAccessToken);
}