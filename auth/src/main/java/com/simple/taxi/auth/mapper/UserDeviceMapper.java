package com.simple.taxi.auth.mapper;

import com.simple.taxi.auth.model.dto.UserDeviceDTO;
import com.simple.taxi.auth.model.entity.UserDevice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDeviceMapper {
    UserDeviceDTO toDto(UserDevice userDevice);
}