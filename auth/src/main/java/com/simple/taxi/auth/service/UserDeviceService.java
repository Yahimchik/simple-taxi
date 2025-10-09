package com.simple.taxi.auth.service;

import com.simple.taxi.auth.model.dto.UserDeviceDTO;

import java.util.List;
import java.util.UUID;

public interface UserDeviceService {

    List<UserDeviceDTO> getUserDevices(UUID userId);

    List<UserDeviceDTO> getActiveUserDevices(UUID userId);
}