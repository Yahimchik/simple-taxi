package com.simple.taxi.auth.model.dto;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder
public record RegistrationRequest(
        @Email
        @NotBlank
        String email,
        String phone,
        @NotBlank
        @Size(min = 6, max = 100)
        String password
) {
}