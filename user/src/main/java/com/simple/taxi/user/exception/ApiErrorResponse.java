package com.simple.taxi.user.exception;

import com.simple.taxi.user.model.enums.ErrorType;
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