package com.simple.taxi.auth.exception;

import com.simple.taxi.auth.model.enums.ErrorType;

public class CaptchaRequiredException extends ApiException {
    public CaptchaRequiredException(ErrorType errorType, Object... params) {
        super(errorType, errorType.getDescription(), params);
    }
}
