package com.simple.taxi.user.exception;

import com.simple.taxi.user.model.enums.ErrorType;

public class FileException extends ApiException {
    public FileException(ErrorType errorType, Object... params) {
        super(errorType, errorType.getDescription(), params);
    }
}
