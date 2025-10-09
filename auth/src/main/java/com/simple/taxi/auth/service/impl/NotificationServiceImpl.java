package com.simple.taxi.auth.service.impl;

import com.simple.taxi.auth.kafka.EventProducer;
import com.simple.taxi.auth.service.NotificationService;
import com.simple.taxi.dto.NotificationChannel;
import com.simple.taxi.dto.NotificationEvent;
import com.simple.taxi.dto.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final EventProducer eventProducer;

    @Override
    public void sendEmail(UUID userId, String email, NotificationType type, Map<String, Object> params) {
        NotificationEvent event = NotificationEvent.builder()
                .userId(userId.toString())
                .recipient(email)
                .channel(NotificationChannel.EMAIL)
                .type(type)
                .params(params)
                .build();

        eventProducer.sendEvent("notification-topic", userId.toString(), event);
    }
}
