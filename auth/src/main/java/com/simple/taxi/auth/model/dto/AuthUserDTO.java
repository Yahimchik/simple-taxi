package com.simple.taxi.auth.model.dto;

import com.simple.taxi.auth.model.enums.LoginType;
import lombok.*;

@Builder
public record AuthUserDTO(
        String id,
        String accessToken,
        LoginType loginType,
        boolean complete
) {
}