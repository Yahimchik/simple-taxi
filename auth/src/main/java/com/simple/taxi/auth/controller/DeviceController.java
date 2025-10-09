package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.LoggedInUserId;
import com.simple.taxi.auth.model.dto.UserDeviceDTO;
import com.simple.taxi.auth.service.UserDeviceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;

@RestController
@RequestMapping(DEVICE_CONTROLLER)
@RequiredArgsConstructor
public class DeviceController {
    private final UserDeviceService userDeviceService;

    @GetMapping(USER_DEVICES)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<List<UserDeviceDTO>> getUserDevices(@LoggedInUserId UUID userId) {
        return ResponseEntity.ok(userDeviceService.getUserDevices(userId));
    }

    @GetMapping(USER_DEVICES_ACTIVE)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<List<UserDeviceDTO>> getUserDevicesActive(@LoggedInUserId UUID userId) {
        return ResponseEntity.ok(userDeviceService.getActiveUserDevices(userId));
    }
}