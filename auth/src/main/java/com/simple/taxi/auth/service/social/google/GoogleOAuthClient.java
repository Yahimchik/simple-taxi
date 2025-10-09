package com.simple.taxi.auth.service.social.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.simple.taxi.auth.model.dto.AuthRequest;
import com.simple.taxi.auth.model.dto.GoogleTokenData;
import com.simple.taxi.auth.service.social.OAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.google.api.client.json.gson.GsonFactory.getDefaultInstance;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient implements OAuthClient<GoogleTokenData> {
    @Value("${google.client-id}")
    private String id;

    @Value("${google.client-secret}")
    private String secret;

    @Override
    public String getClientId() {
        return id;
    }

    @Override
    public GoogleTokenData exchangeCodeForToken(AuthRequest r) {
        try {
            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(), getDefaultInstance(), id, secret, r.code(), r.redirectUri())
                    .execute();

            GoogleTokenData tokenData = new GoogleTokenData();
            tokenData.setAccess_token(response.getAccessToken());
            tokenData.setScope(response.getScope());
            return tokenData;

        } catch (Exception e) {
            throw new RuntimeException("Google token exchange error", e);
        }
    }
}