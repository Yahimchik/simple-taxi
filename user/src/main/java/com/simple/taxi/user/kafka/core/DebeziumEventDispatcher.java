package com.simple.taxi.user.kafka.core;

import com.simple.taxi.user.model.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebeziumEventDispatcher {

    private final Map<String, EntityChangeHandler> handlerRegistry;

    public void dispatch(String topic, UserEvent event) {
        EntityChangeHandler handler = handlerRegistry.get(topic);
        if (handler == null) {
            log.warn("⚠️ No handler registered for topic {}", topic);
            return;
        }

        var payload = event.getPayload();
        if (payload == null) {
            log.warn("⚠️ Skipping event with empty payload for topic {}", topic);
            return;
        }

        switch (payload.getOp()) {
            case "c" -> handler.onCreate(payload.getAfter());
            case "u" -> handler.onUpdate(payload.getAfter(), payload.getBefore());
            case "d" -> handler.onDelete(payload.getBefore());
            default -> log.warn("⚠️ Unknown op '{}' for topic {}", payload.getOp(), topic);
        }
    }
}

