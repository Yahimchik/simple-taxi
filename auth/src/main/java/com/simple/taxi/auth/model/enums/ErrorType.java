package com.simple.taxi.auth.model.enums;

import lombok.Getter;

@Getter
public enum ErrorType {
    ACCESS_DENIED("Access denied"),

    CAPTCHA_REQUIRED("Captcha required"),

    FIELD_VALIDATION("Field validation error"),

    INTERNAL_ERROR("Internal server error"),
    INVALID_CREDENTIALS("Invalid email/phone or password"),

    NEW_PASSWORD_EQUALS_TO_OLD("New password is equal to old"),
    NO_IMPLEMENTATION_FOR_LOGIN_TYPE("No implementation for login type"),
    NO_SUCH_ROLE_AT_USER("No such role at user"),

    REFRESH_TOKEN_EXPIRED("Refresh token has expired"),
    REFRESH_TOKEN_INVALID("Refresh token is invalid"),
    ROLE_NOT_FOUND("Role not found"),

    TOKEN_NOT_FOUND("Token not found"),
    TWO_FACTOR_REQUIRED("Two factor required"),

    USER_ALREADY_ACTIVE("User is already active"),
    USER_BLOCKED("User is blocked"),
    USER_CONFIRM_PASSWORD_NOT_MATCH("Password and confirmation do not match"),
    USER_DEVICE_NOT_FOUND("User device not found"),
    USER_EMAIL_EXISTS("User with this email already exists"),
    USER_EMAIL_INVALID("Email address is invalid"),
    USER_NOT_ACTIVATED("User is not activated"),
    USER_NOT_FOUND("User not found"),
    USER_PHONE_EXISTS("User with this phone already exists"),
    UNSUPPORTED_LOGIN_TYPE("Unsupported login type"),

    VERIFICATION_TOKEN_EXPIRED("Verification token has expired"),
    VERIFICATION_TOKEN_NOT_FOUND("Verification token not found"),
    VERIFICATION_TOKEN_TOO_FREQUENT("Verification token requested too frequently");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }
}