package com.simple.taxi.auth.mapper;

import com.simple.taxi.auth.model.dto.AuthUserDTO;
import com.simple.taxi.auth.model.entity.AuthUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {
    AuthUserDTO toDto(AuthUser authUser);
}
