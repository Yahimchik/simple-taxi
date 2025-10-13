package com.simple.taxi.user.exception;

import com.simple.taxi.user.model.enums.ErrorType;

public class NotFoundException extends ApiException {
    public NotFoundException(ErrorType errorType, Object... params) {
        super(errorType, errorType.getDescription(), params);
    }
}