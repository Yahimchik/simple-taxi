package com.simple.taxi.user.mapper;

import com.simple.taxi.user.model.dto.UpdateUserProfileRequest;
import com.simple.taxi.user.model.dto.UserEvent;
import com.simple.taxi.user.model.dto.UserProfileDTO;
import com.simple.taxi.user.model.entities.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    UserProfile toEntity(UserEvent.User user);

    UserProfileDTO toDto(UserProfile entity);

    void updateEntity(@MappingTarget UserProfile entity, UpdateUserProfileRequest request);
}
