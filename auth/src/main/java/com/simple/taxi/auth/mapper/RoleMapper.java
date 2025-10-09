package com.simple.taxi.auth.mapper;

import com.simple.taxi.auth.model.entity.Role;
import com.simple.taxi.auth.model.enums.RoleEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Named("mapToEnum")
    default RoleEnum mapToEnum(Role role) {
        return role != null ? role.getName() : null;
    }
}