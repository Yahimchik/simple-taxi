package com.simple.taxi.user.model.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserProfileDTO(
        UUID id,
        String firstName,
        String lastName,
        String avatarUrl,
        String email,
        String phone,
        Instant createdAt,
        Instant updatedAt
) {}
