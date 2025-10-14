package com.simple.taxi.user.controller;

import com.simple.taxi.user.model.dto.UpdateSettingsRequest;
import com.simple.taxi.user.model.dto.UserSettingsDTO;
import com.simple.taxi.user.model.enums.Language;
import com.simple.taxi.user.model.enums.PaymentMethod;
import com.simple.taxi.user.model.enums.Theme;
import com.simple.taxi.user.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping("/{userId}")
    public Mono<UserSettingsDTO> getUserSettings(@PathVariable UUID userId) {
        return userSettingsService.getSettingsByUserId(userId);
    }

    @PutMapping("/{userId}")
    public Mono<UserSettingsDTO> updateUserSettings(
            @PathVariable UUID userId,
            @RequestBody UpdateSettingsRequest request
    ) {
        return userSettingsService.updateSettings(userId, request);
    }

    @DeleteMapping("/{userId}/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> resetUserSettings(@PathVariable UUID userId) {
        return userSettingsService.resetSettings(userId);
    }

    @PatchMapping("/{userId}/language")
    public Mono<UserSettingsDTO> updateLanguage(
            @PathVariable UUID userId,
            @RequestParam Language language
    ) {
        return userSettingsService.updateLanguage(userId, language);
    }

    @PatchMapping("/{userId}/theme")
    public Mono<UserSettingsDTO> updateTheme(
            @PathVariable UUID userId,
            @RequestParam Theme theme
    ) {
        return userSettingsService.updateTheme(userId, theme);
    }

    @PatchMapping("/{userId}/payment-method")
    public Mono<UserSettingsDTO> updatePaymentMethod(
            @PathVariable UUID userId,
            @RequestParam PaymentMethod paymentMethod
    ) {
        return userSettingsService.updatePaymentMethod(userId, paymentMethod);
    }

    @PatchMapping("/{userId}/push-enabled")
    public Mono<UserSettingsDTO> updatePushEnabled(
            @PathVariable UUID userId,
            @RequestParam boolean enabled
    ) {
        return userSettingsService.updatePushEnabled(userId, enabled);
    }

    @PatchMapping("/{userId}/email-notifications")
    public Mono<UserSettingsDTO> updateEmailNotifications(
            @PathVariable UUID userId,
            @RequestParam boolean enabled
    ) {
        return userSettingsService.updateEmailNotifications(userId, enabled);
    }


    @PostMapping("/{userId}/reset-language")
    public Mono<UserSettingsDTO> resetLanguage(@PathVariable UUID userId) {
        return userSettingsService.resetLanguage(userId);
    }

    @PostMapping("/{userId}/reset-theme")
    public Mono<UserSettingsDTO> resetTheme(@PathVariable UUID userId) {
        return userSettingsService.resetTheme(userId);
    }

    @GetMapping("/default")
    public Mono<UserSettingsDTO> getDefaultSettings() {
        return userSettingsService.getDefaultSettings();
    }
}