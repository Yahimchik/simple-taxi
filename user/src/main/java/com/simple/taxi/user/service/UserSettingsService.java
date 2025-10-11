package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.UpdateSettingsRequest;
import com.simple.taxi.user.model.dto.UserSettingsDTO;
import com.simple.taxi.user.model.enums.Language;
import com.simple.taxi.user.model.enums.PaymentMethod;
import com.simple.taxi.user.model.enums.Theme;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserSettingsService {
    Mono<UserSettingsDTO> getSettingsByUserId(UUID userId);

    Mono<UserSettingsDTO> updateSettings(UUID userId, UpdateSettingsRequest request);

    Mono<Void> resetSettings(UUID userId);

    Mono<UserSettingsDTO> updateLanguage(UUID userId, Language language);

    Mono<UserSettingsDTO> updateTheme(UUID userId, Theme theme);

    Mono<UserSettingsDTO> updatePaymentMethod(UUID userId, PaymentMethod paymentMethod);

    Mono<Boolean> existsByUserId(UUID userId);

    Mono<UserSettingsDTO> updatePushEnabled(UUID userId, boolean enabled);

    Mono<UserSettingsDTO> updateEmailNotifications(UUID userId, boolean enabled);

    Mono<UserSettingsDTO> resetLanguage(UUID userId);

    Mono<UserSettingsDTO> resetTheme(UUID userId);

    Mono<UserSettingsDTO> getDefaultSettings();

}
