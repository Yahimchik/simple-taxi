package com.simple.taxi.user.exception;

import com.simple.taxi.user.model.enums.ErrorType;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public abstract class ApiException extends RuntimeException {
    private final ErrorType errorType;
    private final Object[] params;
    private final ZonedDateTime time;

    public ApiException(ErrorType errorType, String message, Object... params) {
        super(message);
        this.errorType = errorType;
        this.params = params;
        this.time = ZonedDateTime.now();
    }
}

