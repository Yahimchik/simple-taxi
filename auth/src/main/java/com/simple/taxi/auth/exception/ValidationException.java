package com.simple.taxi.auth.exception;

import com.simple.taxi.auth.model.enums.ErrorType;

public class ValidationException extends ApiException {
    public ValidationException(ErrorType errorType, Object... params) {
        super(errorType, errorType.getDescription(), params);
    }
}