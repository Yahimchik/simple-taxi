package com.simple.taxi.user.kafka.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.taxi.user.kafka.core.DebeziumEventDispatcher;
import com.simple.taxi.user.model.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserListener {

    private final ObjectMapper objectMapper;
    private final DebeziumEventDispatcher dispatcher;

    @KafkaListener(topicPattern = "auth\\.public\\..*", groupId = "user-service")
    public void listen(@Header("kafka_receivedTopic") String topic, String message) {
        try {
            UserEvent event = objectMapper.readValue(message, UserEvent.class);
            dispatcher.dispatch(topic, event);
        } catch (Exception e) {
            log.error("❌ Failed to process Debezium event from topic {}", topic, e);
        }
    }
}