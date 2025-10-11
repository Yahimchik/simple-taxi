package com.simple.taxi.user.model.dto;

import lombok.Builder;

@Builder
public record UpdateUserProfileRequest(
        String firstName,
        String lastName,
        String avatarUrl
) {}
