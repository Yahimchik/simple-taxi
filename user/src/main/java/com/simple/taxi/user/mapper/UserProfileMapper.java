package com.simple.taxi.user.mapper;

import com.simple.taxi.user.model.dto.UserCreatedEvent;
import com.simple.taxi.user.model.entities.UserProfile;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    UserProfile toEntity(UserCreatedEvent.User user);
}
