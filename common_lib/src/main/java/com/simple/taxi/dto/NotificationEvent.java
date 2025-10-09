package com.simple.taxi.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record NotificationEvent(
        String userId,
        String recipient,
        NotificationChannel channel,
        NotificationType type,
        Map<String, Object> params
) {
}