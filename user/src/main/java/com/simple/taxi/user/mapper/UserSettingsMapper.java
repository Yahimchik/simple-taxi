package com.simple.taxi.user.mapper;

import com.simple.taxi.user.model.dto.UpdateSettingsRequest;
import com.simple.taxi.user.model.dto.UpdateUserProfileRequest;
import com.simple.taxi.user.model.dto.UserEvent;
import com.simple.taxi.user.model.dto.UserSettingsDTO;
import com.simple.taxi.user.model.entities.UserProfile;
import com.simple.taxi.user.model.entities.UserSettings;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserSettingsMapper {
    UserSettings toEntity(UserEvent.User user);

    UserSettingsDTO toDto(UserSettings settings);

    void updateEntity(@MappingTarget UserSettings entity, UpdateSettingsRequest request);
}