package com.simple.taxi.user.model.enums;

import lombok.Getter;

@Getter
public enum ErrorType {
    ACCESS_DENIED("Access denied"),

    FIELD_VALIDATION("Field validation error"),

    INTERNAL_ERROR("Internal server error"),

    USER_PROFILE_NOT_FOUND("User profile not found");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }
}