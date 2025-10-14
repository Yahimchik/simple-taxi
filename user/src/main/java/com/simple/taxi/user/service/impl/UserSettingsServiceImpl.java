package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.exception.NotFoundException;
import com.simple.taxi.user.mapper.UserSettingsMapper;
import com.simple.taxi.user.model.dto.UpdateSettingsRequest;
import com.simple.taxi.user.model.dto.UserSettingsDTO;
import com.simple.taxi.user.model.entities.UserSettings;
import com.simple.taxi.user.model.enums.Language;
import com.simple.taxi.user.model.enums.PaymentMethod;
import com.simple.taxi.user.model.enums.Theme;
import com.simple.taxi.user.repository.UserSettingsRepository;
import com.simple.taxi.user.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.simple.taxi.user.model.enums.ErrorType.SETTINGS_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsRepository repository;
    private final UserSettingsMapper mapper;

    @Override
    public Mono<UserSettingsDTO> getSettingsByUserId(UUID userId) {
        return repository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException(SETTINGS_NOT_FOUND, userId)))
                .map(mapper::toDto);
    }

    @Override
    public Mono<UserSettingsDTO> updateSettings(UUID userId, UpdateSettingsRequest request) {
        return repository.findById(userId)
                .flatMap(settings -> {
                    mapper.updateEntity(settings, request);
                    return repository.save(settings);
                })
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new NotFoundException(SETTINGS_NOT_FOUND, userId)));
    }

    @Override
    public Mono<Void> resetSettings(UUID userId) {
        return repository.findById(userId)
                .flatMap(settings -> {
                    UserSettings defaults = getDefaultEntity(userId);
                    defaults.setId(settings.getId());
                    return repository.save(defaults);
                })
                .then();
    }

    @Override
    public Mono<UserSettingsDTO> updateLanguage(UUID userId, Language language) {
        return updateSingleField(userId, settings -> settings.setLanguage(language));
    }

    @Override
    public Mono<UserSettingsDTO> updateTheme(UUID userId, Theme theme) {
        return updateSingleField(userId, settings -> settings.setTheme(theme));
    }

    @Override
    public Mono<UserSettingsDTO> updatePaymentMethod(UUID userId, PaymentMethod paymentMethod) {
        return updateSingleField(userId, settings -> settings.setPaymentMethod(paymentMethod));
    }

    @Override
    public Mono<Boolean> existsByUserId(UUID userId) {
        return repository.existsById(userId);
    }

    @Override
    public Mono<UserSettingsDTO> updatePushEnabled(UUID userId, boolean enabled) {
        return updateSingleField(userId, settings -> settings.setPushEnabled(enabled));
    }

    @Override
    public Mono<UserSettingsDTO> updateEmailNotifications(UUID userId, boolean enabled) {
        return updateSingleField(userId, settings -> settings.setEmailNotifications(enabled));
    }

    @Override
    public Mono<UserSettingsDTO> resetLanguage(UUID userId) {
        return updateSingleField(userId, settings -> settings.setLanguage(Language.RU));
    }

    @Override
    public Mono<UserSettingsDTO> resetTheme(UUID userId) {
        return updateSingleField(userId, settings -> settings.setTheme(Theme.LIGHT));
    }

    @Override
    public Mono<UserSettingsDTO> getDefaultSettings() {
        return Mono.just(mapper.toDto(getDefaultEntity(null)));
    }

    private Mono<UserSettingsDTO> updateSingleField(UUID userId, java.util.function.Consumer<UserSettings> updater) {
        return repository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException(SETTINGS_NOT_FOUND, userId)))
                .flatMap(settings -> {
                    updater.accept(settings);
                    return repository.save(settings);
                })
                .map(mapper::toDto);
    }

    private UserSettings getDefaultEntity(UUID userId) {
        UserSettings settings = new UserSettings();
        settings.setId(userId);
        settings.setLanguage(Language.RU);
        settings.setTheme(Theme.LIGHT);
        settings.setPaymentMethod(PaymentMethod.CARD);
        settings.setPushEnabled(true);
        settings.setEmailNotifications(true);
        return settings;
    }
}
