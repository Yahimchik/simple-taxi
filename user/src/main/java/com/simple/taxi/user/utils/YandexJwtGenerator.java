package com.simple.taxi.user.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class YandexJwtGenerator {

    static {
        // Регистрируем BouncyCastle провайдер один раз
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String generateJwt(Map<String, Object> keyJson) throws Exception {
        String privateKeyPem = (String) keyJson.get("private_key");

        privateKeyPem = privateKeyPem
                .replace("PLEASEDONOTREMOVETHISLINE!", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        // Декодируем Base64 и создаём PrivateKey через BouncyCastle
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey privateKey = KeyFactory.getInstance("RSA", "BC").generatePrivate(spec);

        Instant now = Instant.now();

        // Строим JWT с PS256
        return Jwts.builder()
                .setHeaderParam("alg", "PS256")
                .setIssuer((String) keyJson.get("service_account_id"))
                .setAudience("https://iam.api.cloud.yandex.net/iam/v1/tokens")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(3600)))
                .signWith(privateKey, SignatureAlgorithm.PS256)
                .compact();
    }
}
