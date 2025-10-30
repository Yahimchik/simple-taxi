package com.simple.taxi.user.controller;

import com.simple.taxi.user.model.dto.UpdateUserProfileRequest;
import com.simple.taxi.user.model.dto.UserProfileDTO;
import com.simple.taxi.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.simpletaxi.internalauthstarter.security.PublicEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.simple.taxi.user.constant.UrlConstant.ID;
import static com.simple.taxi.user.constant.UrlConstant.USER_PROFILE_API;

@RestController
@RequestMapping(USER_PROFILE_API)
@RequiredArgsConstructor
@Tag(name = "User Profiles", description = "User profile management API")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping(ID)
    @Operation(summary = "Get user profile by ID")
    public Mono<ResponseEntity<UserProfileDTO>> getProfileByUserId(
            @Parameter(description = "User ID") @PathVariable UUID id) {
        return userProfileService.getProfileByUserId(id)
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

    @PutMapping(ID)
    @Operation(summary = "Update user profile information")
    public Mono<ResponseEntity<UserProfileDTO>> updateProfile(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @RequestBody UpdateUserProfileRequest request) {
        return userProfileService.updateProfile(id, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/avatar")
    @Operation(summary = "Update user avatar")
    public Mono<ResponseEntity<UserProfileDTO>> updateAvatar(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Parameter(description = "New avatar URL") @RequestParam UUID avatarId) {
        return userProfileService.updateAvatar(id, avatarId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/phone")
    @Operation(summary = "Update user phone number")
    public Mono<ResponseEntity<UserProfileDTO>> updatePhone(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Parameter(description = "New phone number") @RequestParam String phone) {
        return userProfileService.updatePhone(id, phone)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Search user profiles by first or last name")
    @PublicEndpoint
    public Flux<UserProfileDTO> searchProfiles(
            @Parameter(description = "Search query (first or last name)") @RequestParam String query) {
        return userProfileService.searchProfiles(query);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user profile by ID")
    public Mono<ResponseEntity<Void>> deleteProfile(
            @Parameter(description = "User ID") @PathVariable UUID id) {
        return userProfileService.deleteProfileByUserId(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}