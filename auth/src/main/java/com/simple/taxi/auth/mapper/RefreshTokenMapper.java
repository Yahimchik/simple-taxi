package com.simple.taxi.auth.mapper;

import com.simple.taxi.auth.model.dto.RefreshTokenDTO;
import com.simple.taxi.auth.model.entity.RefreshToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserDeviceMapper.class})
public interface RefreshTokenMapper {
    RefreshTokenDTO toDto(RefreshToken refreshToken);
}