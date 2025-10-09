package com.simple.taxi.auth.service.social;

import com.simple.taxi.auth.model.dto.AuthRequest;
import com.simple.taxi.auth.model.dto.SocialTokenData;

public interface OAuthClient<T extends SocialTokenData> {
    T exchangeCodeForToken(AuthRequest request);

    String getClientId();
}