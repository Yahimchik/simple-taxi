package com.simple.taxi.user.config;

import com.simple.taxi.user.kafka.core.EntityChangeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class DebeziumConfig {

    private final List<EntityChangeHandler> handlers;

    @Bean
    public Map<String, EntityChangeHandler> handlerRegistry() {
        return handlers.stream()
                .filter(handler -> handler.getTopic() != null && !handler.getTopic().isBlank())
                .collect(Collectors.toMap(EntityChangeHandler::getTopic, Function.identity()));
    }
}
