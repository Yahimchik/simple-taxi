package com.simple.taxi.auth.service.social;

import com.simple.taxi.auth.model.dto.AuthRequest;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.enums.LoginType;

public interface SocialAuthProvider {
    User authenticate(AuthRequest request);

    LoginType getType();

    SocialUserService getSocialUserService();

    OAuthClient<?> getOAuthClient();
}