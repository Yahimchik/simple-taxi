package com.simple.taxi.auth.exception;

import com.simple.taxi.auth.model.enums.ErrorType;

public class AuthException extends ApiException {
  public AuthException(ErrorType errorType, Object... params) {
    super(errorType, errorType.getDescription(), params);
  }
}
