package com.simple.taxi.auth.mapper;

import com.simple.taxi.auth.model.dto.UserDTO;
import com.simple.taxi.auth.model.entity.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        uses = {RoleMapper.class, AuthUserMapper.class, UserDeviceMapper.class}
)
public interface UserMapper {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapToEnum")
    UserDTO toDto(User user);
}