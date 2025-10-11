package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.model.dto.UpdateSettingsRequest;
import com.simple.taxi.user.model.dto.UserSettingsDTO;
import com.simple.taxi.user.model.enums.Language;
import com.simple.taxi.user.model.enums.PaymentMethod;
import com.simple.taxi.user.model.enums.Theme;
import com.simple.taxi.user.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {
    @Override
    public Mono<UserSettingsDTO> getSettingsByUserId(UUID userId) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> updateSettings(UUID userId, UpdateSettingsRequest request) {
        return null;
    }

    @Override
    public Mono<Void> resetSettings(UUID userId) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> updateLanguage(UUID userId, Language language) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> updateTheme(UUID userId, Theme theme) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> updatePaymentMethod(UUID userId, PaymentMethod paymentMethod) {
        return null;
    }

    @Override
    public Mono<Boolean> existsByUserId(UUID userId) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> updatePushEnabled(UUID userId, boolean enabled) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> updateEmailNotifications(UUID userId, boolean enabled) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> resetLanguage(UUID userId) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> resetTheme(UUID userId) {
        return null;
    }

    @Override
    public Mono<UserSettingsDTO> getDefaultSettings() {
        return null;
    }
}
