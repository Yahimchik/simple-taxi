package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.UpdateUserProfileRequest;
import com.simple.taxi.user.model.dto.UserProfileDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserProfileService {

    Mono<UserProfileDTO> getProfileByUserId(UUID userId);

    Mono<UserProfileDTO> updateProfile(UUID userId, UpdateUserProfileRequest request);

    Mono<Void> deleteProfileByUserId(UUID userId);

    Flux<UserProfileDTO> searchProfiles(String query);

    Mono<Boolean> existsByUserId(UUID userId);

    Mono<UserProfileDTO> getProfileByEmail(String email);

    Mono<UserProfileDTO> getProfileByPhone(String phone);

    Mono<UserProfileDTO> updateAvatar(UUID userId, String avatarUrl);

    Mono<UserProfileDTO> updatePhone(UUID userId, String phone);
}
