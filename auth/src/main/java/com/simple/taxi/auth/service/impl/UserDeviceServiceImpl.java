package com.simple.taxi.auth.service.impl;

import com.simple.taxi.auth.exception.NotFoundException;
import com.simple.taxi.auth.mapper.UserDeviceMapper;
import com.simple.taxi.auth.model.dto.UserDeviceDTO;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.UserDevice;
import com.simple.taxi.auth.repository.UserDeviceRepository;
import com.simple.taxi.auth.repository.UserRepository;
import com.simple.taxi.auth.service.UserDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.simple.taxi.auth.model.enums.ErrorType.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final UserRepository userRepository;
    private final UserDeviceMapper userDeviceMapper;

    @Override
    public List<UserDeviceDTO> getUserDevices(UUID userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        List<UserDevice> devices = userDeviceRepository.findUserDeviceByUser(user);

        return devices.stream()
                .map(userDeviceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDeviceDTO> getActiveUserDevices(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        List<UserDevice> devices = userDeviceRepository.findUserDeviceByUser(user);

        List<UserDevice> activeDevices = devices.stream()
                .filter(device -> device.getRefreshTokens().stream()
                        .anyMatch(token -> !token.isRevoked()))
                .toList();

        return activeDevices.stream()
                .map(userDeviceMapper::toDto)
                .collect(Collectors.toList());
    }
}