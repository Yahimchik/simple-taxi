package com.simple.taxi.auth.service;

import com.simple.taxi.dto.NotificationType;

import java.util.Map;
import java.util.UUID;

public interface NotificationService {
    void sendEmail(UUID userId, String email, NotificationType type, Map<String, Object> params);
}
