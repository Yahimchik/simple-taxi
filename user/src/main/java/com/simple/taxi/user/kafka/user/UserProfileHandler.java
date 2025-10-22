package com.simple.taxi.user.kafka.user;

import com.simple.taxi.user.kafka.core.AbstractEntityChangeHandler;
import com.simple.taxi.user.mapper.UserProfileMapper;
import com.simple.taxi.user.mapper.UserSettingsMapper;
import com.simple.taxi.user.model.dto.UserEvent;
import com.simple.taxi.user.model.entities.UserProfile;
import com.simple.taxi.user.model.entities.UserSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserProfileHandler extends AbstractEntityChangeHandler {

    public static final String AUTH_PUBLIC_USERS = "auth.public.users";
    public static final String USER_ID = "id";
    private final UserProfileMapper mapper;
    private final UserSettingsMapper userSettingsMapper;

    public UserProfileHandler(R2dbcEntityTemplate template, UserProfileMapper mapper, UserSettingsMapper userSettingsMapper) {
        super(template);
        this.mapper = mapper;
        this.userSettingsMapper = userSettingsMapper;
    }

    @Override
    public String getTopic() {
        return AUTH_PUBLIC_USERS;
    }

    @Override
    public void onCreate(Object after) {
        var user = (UserEvent.User) after;

        var profile = mapper.toEntity(user);
        var settings = userSettingsMapper.toEntity(user);

        Mono.when(template.insert(UserProfile.class).using(profile), template.insert(UserSettings.class).using(settings))
                .doOnSuccess(v -> log.info("✅ Created profile + settings for user {}", user.getId()))
                .subscribe();
    }

    @Override
    public void onUpdate(Object after, Object before) {
        var userAfter = (UserEvent.User) after;
        var userBefore = (UserEvent.User) before;
        applyPartialUpdate(UserProfile.class, USER_ID, userAfter.getId(), userAfter, userBefore);
    }

    @Override
    public void onDelete(Object before) {
        var user = (UserEvent.User) before;
        deleteById(user.getId(), UserProfile.class, UserSettings.class);
    }
}