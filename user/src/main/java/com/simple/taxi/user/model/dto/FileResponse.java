package com.simple.taxi.user.model.dto;

import java.util.UUID;

public record FileResponse(
        UUID id,
        String fileName,
        String originalFileName,
        String url
) {
}