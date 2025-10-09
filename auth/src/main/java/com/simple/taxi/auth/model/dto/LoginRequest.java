package com.simple.taxi.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
public record LoginRequest(
        @NotBlank String emailOrPhone,
        @NotBlank String password,
        @NotBlank String deviceId
) {
}