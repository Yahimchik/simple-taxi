package com.simple.taxi.user.model.dto;

import java.util.UUID;

public record FileNameAndUUIDAtStorage (
        UUID id,
        String fileName
){
}
