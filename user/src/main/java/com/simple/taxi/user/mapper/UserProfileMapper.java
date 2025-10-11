package com.simple.taxi.user.mapper;

import com.simple.taxi.user.model.dto.UserEvent;
import com.simple.taxi.user.model.entities.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    UserProfile toEntity(UserEvent.User user);
}
