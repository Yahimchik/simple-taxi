package com.simple.taxi.user.model.dto;

import java.util.UUID;

public record FileInfoDto (
        UUID id,
        String url,
        String fileName
){
}