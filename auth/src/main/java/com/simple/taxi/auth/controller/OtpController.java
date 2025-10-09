package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.DeviceInfo;
import com.simple.taxi.auth.config.argument_resolver.LoggedInUserId;
import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.service.OtpService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;

@RestController
@RequestMapping(OTP_CONTROLLER)
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping(DISABLE_2FA)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<String> disable2fa(@LoggedInUserId UUID userId) {
        otpService.disable2fa(userId);
        return ResponseEntity.ok("2FA disabled");
    }

    @PostMapping(ENABLE_2FA)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<String> enable2fa(@LoggedInUserId UUID userId) {
        otpService.enable2fa(userId);
        return ResponseEntity.ok("2FA enabled");
    }

    @PostMapping(REQUEST_OTP)
    public ResponseEntity<String> requestOtp(@RequestParam String email) {
        return ResponseEntity.ok(otpService.generateOtp(email) + "OTP sent to email");
    }

    @PostMapping(VERIFY_OTP)
    public ResponseEntity<TokenResponse> verifyOtp(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String deviceId,
            @DeviceInfo String deviceInfo
    ) {
        TokenResponse response = otpService.verifyOtpAndLogin(email, code, deviceId, deviceInfo);
        return ResponseEntity.ok(response);
    }
}