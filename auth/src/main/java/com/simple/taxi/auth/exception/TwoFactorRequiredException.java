package com.simple.taxi.auth.exception;

import com.simple.taxi.auth.model.enums.ErrorType;

public class TwoFactorRequiredException extends ApiException {
    public TwoFactorRequiredException(ErrorType errorType, Object... params) {
        super(errorType, errorType.getDescription(), params);
    }
}
