package com.simple.taxi.auth.service.social;

import com.simple.taxi.auth.model.dto.SocialTokenData;
import com.simple.taxi.auth.model.dto.SocialUser;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.enums.LoginType;

public interface SocialUserService {
    LoginType getLoginType();
    User createUser(SocialUser socialUser);
    SocialUser findUser(SocialTokenData socialTokenData);
}