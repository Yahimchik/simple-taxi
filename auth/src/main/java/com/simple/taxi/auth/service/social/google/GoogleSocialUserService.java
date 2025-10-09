package com.simple.taxi.auth.service.social.google;

import com.simple.taxi.auth.exception.NotFoundException;
import com.simple.taxi.auth.model.dto.SocialTokenData;
import com.simple.taxi.auth.model.dto.SocialUser;
import com.simple.taxi.auth.model.entity.AuthUser;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.enums.LoginType;
import com.simple.taxi.auth.repository.AuthUserRepository;
import com.simple.taxi.auth.repository.RoleRepository;
import com.simple.taxi.auth.service.social.SocialUserService;
import com.simple.taxi.auth.util.UuidGenerator;
import com.simple.taxi.auth.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.simple.taxi.auth.model.enums.ErrorType.ROLE_NOT_FOUND;
import static com.simple.taxi.auth.model.enums.LoginType.GOOGLE;
import static com.simple.taxi.auth.model.enums.RoleEnum.USER;
import static com.simple.taxi.auth.model.enums.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class GoogleSocialUserService implements SocialUserService {

    public static final String GOOGLEAPIS_COM_OAUTH_2_V_3_USERINFO = "https://www.googleapis.com/oauth2/v3/userinfo";
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final UuidGenerator uuidGenerator;
    private final RestTemplate restTemplate;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginType getLoginType() {
        return GOOGLE;
    }

    @Override
    public User createUser(SocialUser socialUser) {
        GoogleProfile profile = getUserInfo(socialUser.accessToken());

        var roles = roleRepository.findByName(USER)
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));

        User user = User.builder()
                .id(uuidGenerator.generateV7())
                .email(profile.email())
                .passwordHash(passwordEncoder.encode(passwordValidator.generatePasswordFromEmail(profile.email())))
                .status(ACTIVE)
                .roles(Set.of(roles))
                .authUsers(new HashSet<>())
                .userDevices(new HashSet<>())
                .build();

        AuthUser authUser = AuthUser.builder()
                .id(profile.sub())
                .accessToken(socialUser.accessToken())
                .loginType(GOOGLE)
                .user(user)
                .complete(true)
                .build();

        user.getAuthUsers().add(authUser);
        return user;
    }

    @Override
    public SocialUser findUser(SocialTokenData socialTokenData) {
        GoogleProfile profile = getUserInfo(socialTokenData.getToken());
        String socialId = profile.sub();

        Optional<AuthUser> authUserOpt = authUserRepository.findByIdAndLoginType(socialId, GOOGLE);

        User user = authUserOpt.filter(AuthUser::isComplete)
                .map(AuthUser::getUser)
                .orElse(null);

        return new SocialUser(user, socialId, socialTokenData.getToken());
    }

    public GoogleProfile getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<GoogleProfile> response = restTemplate.exchange(
                GOOGLEAPIS_COM_OAUTH_2_V_3_USERINFO,
                HttpMethod.GET,
                entity,
                GoogleProfile.class
        );
        return response.getBody();
    }

    public record GoogleProfile(
            String sub,
            String email,
            String given_name,
            String family_name,
            String picture
    ) {}
}