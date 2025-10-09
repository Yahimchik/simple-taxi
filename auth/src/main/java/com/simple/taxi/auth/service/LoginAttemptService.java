package com.simple.taxi.auth.service;

import com.simple.taxi.auth.model.dto.LoginContext;
import com.simple.taxi.auth.service.impl.LoginAttemptServiceImpl.AttemptStatus;


public interface LoginAttemptService {
    void loginSucceeded(LoginContext context);

    void loginFailed(String email, String deviceId, String deviceInfo, String ip);

    boolean isBlocked(LoginContext context);

    boolean isCaptchaRequired(LoginContext context);

    String getBlockRemainingReadable(String redisKey);

    AttemptStatus getStatus(String email, String deviceId, String ip);

    void clearAttempts(String email, String deviceId, String ip);
}