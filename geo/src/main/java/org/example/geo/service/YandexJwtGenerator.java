package org.example.geo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

public class YandexJwtGenerator {

    public static String generateJwt(Map<String, Object> keyJson) throws Exception {
        String keyId = (String) keyJson.get("id");
        String serviceAccountId = (String) keyJson.get("service_account_id");
        String privateKeyPem = (String) keyJson.get("private_key");

        PrivateKey privateKey = loadPrivateKey(privateKeyPem);

        Instant now = Instant.now();

        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setIssuer(serviceAccountId)
                .setAudience("https://iam.api.cloud.yandex.net/iam/v1/tokens")
                .setIssuedAt(java.util.Date.from(now))
                .setExpiration(java.util.Date.from(now.plusSeconds(3600)))
                .signWith(privateKey, SignatureAlgorithm.PS256)
                .compact();
    }

    private static PrivateKey loadPrivateKey(String privateKeyPem) throws Exception {
        String privateKeyContent = privateKeyPem
                .replaceAll("(?m)^PLEASE DO NOT REMOVE THIS LINE!.*$", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}
