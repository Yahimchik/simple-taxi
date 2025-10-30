package org.simpletaxi.internalauthstarter.service.impl;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.simpletaxi.internalauthstarter.service.InternalJwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Map;

@Service
public class InternalJwtServiceImpl implements InternalJwtService {

    private final Key internalKey;

    public InternalJwtServiceImpl(@Value("${internal.jwt.secret}") String secret) {
        this.internalKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String generateInternalToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(internalKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Map<String, Object> validateInternalToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(internalKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid internal token", e);
        }
    }
}
