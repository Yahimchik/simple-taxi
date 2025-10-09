package com.simple.taxi.auth.security.impl;

import com.simple.taxi.auth.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtProviderImpl implements JwtProvider {

    private final Key jwtAccessKey;
    private final Key jwtRefreshKey;

    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProviderImpl(
            @Value("${spring.jwt.access.secret}") String accessSecret,
            @Value("${spring.jwt.refresh.secret}") String refreshSecret,
            @Value("${spring.jwt.access.expiration}") long accessTokenExpiration,
            @Value("${spring.jwt.refresh.expiration}") long refreshTokenExpiration
    ) {
        this.jwtAccessKey = Keys.hmacShaKeyFor(accessSecret.getBytes());
        this.jwtRefreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public String generateAccessToken(UUID userId, String role) {
        Instant now = Instant.now();
        Map<String, Object> claims = Map.of("role", role);
        return generateToken(userId, now, accessTokenExpiration, jwtAccessKey, claims);
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        return generateToken(userId, now, refreshTokenExpiration, jwtRefreshKey, Collections.emptyMap());
    }

    @Override
    public UUID getUserIdFromToken(String token, boolean isAccessToken) {
        Claims claims = parseClaims(token, isAccessToken);
        return UUID.fromString(claims.getSubject());
    }

    @Override
    public String getRoleFromAccessToken(String token) {
        Claims claims = parseClaims(token, true);
        return claims.get("role", String.class);
    }

    @Override
    public boolean validateToken(String token, boolean isAccessToken) {
        try {
            Claims claims = parseClaims(token, isAccessToken);
            return !claims.getExpiration().before(Date.from(Instant.now()));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String generateToken(UUID userId, Instant now, long expirationMillis, Key key, Map<String, Object> claims) {
        Instant expiryDate = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .setSubject(userId.toString())
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token, boolean isAccessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(isAccessToken ? jwtAccessKey : jwtRefreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}