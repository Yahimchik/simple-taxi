package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.exception.NotFoundException;
import com.simple.taxi.user.mapper.UserProfileMapper;
import com.simple.taxi.user.model.dto.UpdateUserProfileRequest;
import com.simple.taxi.user.model.dto.UserProfileDTO;
import com.simple.taxi.user.repository.UserProfileRepository;
import com.simple.taxi.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

import static com.simple.taxi.user.model.enums.ErrorType.USER_PROFILE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;
    private final UserProfileMapper mapper;

    @Override
    public Mono<UserProfileDTO> getProfileByUserId(UUID userId) {
        return repository.findById(userId)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(() -> new NotFoundException(USER_PROFILE_NOT_FOUND, userId)));
    }

    @Override
    public Mono<UserProfileDTO> getProfileByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(() -> new NotFoundException(USER_PROFILE_NOT_FOUND, email)));
    }

    @Override
    public Mono<UserProfileDTO> getProfileByPhone(String phone) {
        return repository.findByPhone(phone)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(() -> new NotFoundException(USER_PROFILE_NOT_FOUND, phone)));
    }

    @Override
    public Mono<UserProfileDTO> updateProfile(UUID userId, UpdateUserProfileRequest request) {
        return repository.findById(userId)
                .flatMap(profile -> {
                    mapper.updateEntity(profile, request);
                    profile.setUpdatedAt(Instant.now());
                    return repository.save(profile);
                })
                .map(mapper::toDto)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(USER_PROFILE_NOT_FOUND, userId))));
    }

    @Override
    public Mono<UserProfileDTO> updateAvatar(UUID userId, String avatarUrl) {
        return repository.findById(userId)
                .flatMap(profile -> {
                    profile.setAvatarUrl(avatarUrl);
                    profile.setUpdatedAt(Instant.now());
                    return repository.save(profile);
                })
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(() -> new NotFoundException(USER_PROFILE_NOT_FOUND, userId)));
    }

    @Override
    public Mono<UserProfileDTO> updatePhone(UUID userId, String phone) {
        return repository.findById(userId)
                .flatMap(profile -> {
                    profile.setPhone(phone);
                    profile.setUpdatedAt(Instant.now());
                    return repository.save(profile);
                })
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(() -> new NotFoundException(USER_PROFILE_NOT_FOUND, userId)));
    }

    @Override
    public Mono<Void> deleteProfileByUserId(UUID userId) {
        return existsByUserId(userId)
                .flatMap(exists -> {
                    if (!exists) return Mono.error(new NotFoundException(USER_PROFILE_NOT_FOUND, userId));
                    return repository.deleteById(userId);
                })
                .doOnSuccess(deleted -> log.info("User profile deleted successfully"));

    }

    @Override
    public Flux<UserProfileDTO> searchProfiles(String query) {
        return repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(() -> new NotFoundException(USER_PROFILE_NOT_FOUND)));
    }

    @Override
    public Mono<Boolean> existsByUserId(UUID userId) {
        return repository.existsById(userId);
    }
}
