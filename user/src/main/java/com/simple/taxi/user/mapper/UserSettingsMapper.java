package com.simple.taxi.user.mapper;

import com.simple.taxi.user.model.dto.UserEvent;
import com.simple.taxi.user.model.entities.UserSettings;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSettingsMapper {
    UserSettings toEntity(UserEvent.User user);
}