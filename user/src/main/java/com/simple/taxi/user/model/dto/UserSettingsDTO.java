package com.simple.taxi.user.model.dto;

import com.simple.taxi.user.model.enums.Language;
import com.simple.taxi.user.model.enums.PaymentMethod;
import com.simple.taxi.user.model.enums.Theme;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserSettingsDTO(
        UUID id,
        Language language,
        Boolean pushEnabled,
        Boolean emailNotifications,
        Theme theme,
        PaymentMethod paymentMethod,
        Instant createdAt,
        Instant updatedAt
) {}