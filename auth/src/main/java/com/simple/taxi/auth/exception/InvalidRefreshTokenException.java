package com.simple.taxi.auth.exception;

import com.simple.taxi.auth.model.enums.ErrorType;

public class InvalidRefreshTokenException extends ApiException {
    public InvalidRefreshTokenException(ErrorType errorType, Object... params) {
        super(errorType, errorType.getDescription(), params);
    }
}