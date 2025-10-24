package com.simple.taxi.user.model.enums;

import lombok.Getter;

@Getter
public enum ErrorType {
    ACCESS_DENIED("Access denied"),

    FIELD_VALIDATION("Field validation error"),
    FILE_IMAGE_TYPE("File image type"),
    FILE_PDF_TYPE("File PDF type"),

    INTERNAL_ERROR("Internal server error"),

    SETTINGS_NOT_FOUND("Settings not found"),

    USER_PROFILE_NOT_FOUND("User profile not found"),
    COUNT_OF_PHOTOS_EXCEEDED("Count of photos exceeded");
    private final String description;

    ErrorType(String description) {
        this.description = description;
    }
}