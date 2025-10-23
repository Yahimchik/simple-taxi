package com.simple.taxi.user.model.dto;

import java.util.UUID;

public record FileResponse(
        UUID id,
        String url
) {
}