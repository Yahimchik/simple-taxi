package com.simple.taxi.auth.service;

import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.model.entity.UserOtp;

import java.util.UUID;

public interface OtpService {
    UserOtp generateOtp(String email);

    TokenResponse verifyOtpAndLogin(String email, String code, String deviceId, String deviceInfo);

    void enable2fa(UUID id);

    void disable2fa(UUID id);
}