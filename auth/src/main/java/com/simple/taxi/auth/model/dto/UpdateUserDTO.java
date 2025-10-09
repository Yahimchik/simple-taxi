package com.simple.taxi.auth.model.dto;

import lombok.Builder;

@Builder
public record UpdateUserDTO(
        String email,
        String phoneNumber
) {
}