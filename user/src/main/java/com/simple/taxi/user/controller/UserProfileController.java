package com.simple.taxi.user.controller;

import com.simple.taxi.user.model.dto.UpdateUserProfileRequest;
import com.simple.taxi.user.model.dto.UserProfileDTO;
import com.simple.taxi.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/profiles")
@RequiredArgsConstructor
@Tag(name = "User Profiles", description = "User profile management API")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by ID")
    public Mono<ResponseEntity<UserProfileDTO>> getProfileByUserId(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        return userProfileService.getProfileByUserId(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get user profile by email")
    public Mono<ResponseEntity<UserProfileDTO>> getProfileByEmail(
            @Parameter(description = "User email") @RequestParam String email) {
        return userProfileService.getProfileByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-phone")
    @Operation(summary = "Get user profile by phone number")
    public Mono<ResponseEntity<UserProfileDTO>> getProfileByPhone(
            @Parameter(description = "User phone number") @RequestParam String phone) {
        return userProfileService.getProfileByPhone(phone)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile information")
    public Mono<ResponseEntity<UserProfileDTO>> updateProfile(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestBody UpdateUserProfileRequest request) {
        return userProfileService.updateProfile(userId, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/avatar")
    @Operation(summary = "Update user avatar")
    public Mono<ResponseEntity<UserProfileDTO>> updateAvatar(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "New avatar URL") @RequestParam String avatarUrl) {
        return userProfileService.updateAvatar(userId, avatarUrl)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/phone")
    @Operation(summary = "Update user phone number")
    public Mono<ResponseEntity<UserProfileDTO>> updatePhone(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "New phone number") @RequestParam String phone) {
        return userProfileService.updatePhone(userId, phone)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search user profiles by first or last name")
    public Flux<UserProfileDTO> searchProfiles(
            @Parameter(description = "Search query (first or last name)") @RequestParam String query) {
        return userProfileService.searchProfiles(query);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user profile by ID")
    public Mono<ResponseEntity<Void>> deleteProfile(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        return userProfileService.deleteProfileByUserId(userId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}