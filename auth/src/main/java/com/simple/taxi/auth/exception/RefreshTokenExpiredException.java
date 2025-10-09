package com.simple.taxi.auth.exception;

import com.simple.taxi.auth.model.enums.ErrorType;

public class RefreshTokenExpiredException extends ApiException {
    public RefreshTokenExpiredException(ErrorType errorType, Object... params) {
        super(errorType, errorType.getDescription(), params);
    }
}