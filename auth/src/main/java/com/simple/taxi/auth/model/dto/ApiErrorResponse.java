package com.simple.taxi.auth.model.dto;

import com.simple.taxi.auth.model.enums.ErrorType;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record ApiErrorResponse(
        ErrorType errorType,
        String message,
        ZonedDateTime timestamp,
        Object[] details,
        String path,
        int status
) {
}