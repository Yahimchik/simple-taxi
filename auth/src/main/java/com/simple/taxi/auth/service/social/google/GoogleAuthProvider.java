package com.simple.taxi.auth.service.social.google;

import com.simple.taxi.auth.model.dto.AuthRequest;
import com.simple.taxi.auth.model.dto.GoogleTokenData;
import com.simple.taxi.auth.model.dto.SocialUser;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.enums.LoginType;
import com.simple.taxi.auth.repository.UserRepository;
import com.simple.taxi.auth.service.social.OAuthClient;
import com.simple.taxi.auth.service.social.SocialAuthProvider;
import com.simple.taxi.auth.service.social.SocialUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthProvider implements SocialAuthProvider {

    private final OAuthClient<GoogleTokenData> oAuthClient;
    private final GoogleSocialUserService userService;
    private final UserRepository userRepository;

    @Override
    public User authenticate(AuthRequest request) {
        GoogleTokenData tokenData = oAuthClient.exchangeCodeForToken(request);
        SocialUser socialUser = userService.findUser(tokenData);
        return ensureUserExists(socialUser);
    }

    @Override
    public LoginType getType() {
        return LoginType.GOOGLE;
    }

    @Override
    public SocialUserService getSocialUserService() {
        return userService;
    }

    @Override
    public OAuthClient<?> getOAuthClient() {
        return oAuthClient;
    }

    private User ensureUserExists(SocialUser socialUser) {
        if (socialUser.user() != null) return socialUser.user();
        User user = userService.createUser(socialUser);
        return userRepository.save(user);
    }
}

