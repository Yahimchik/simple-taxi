package com.simple.taxi.user.model.enums;

import lombok.Getter;

@Getter
public enum ErrorType {
    ACCESS_DENIED("Access denied"),

    FIELD_VALIDATION("Field validation error"),
    FILE_IMAGE_TYPE("File image type"),
    FILE_PDF_TYPE("File PDF type"),
    FILE_NOT_FOUND("File not found"),
    FILE_ALREADY_DELETED("File already deleted"),
    FILE_IS_TO_LARGE("File is too large"),

    INTERNAL_ERROR("Internal server error"),

    SETTINGS_NOT_FOUND("Settings not found"),
    FAILED_TO_UPLOAD_FILE("Failed to upload file to MinIO"),
    FAILED_TO_DELETE_FILE("Failed to delete file from MinIO"),
    USER_PROFILE_NOT_FOUND("User profile not found"),
    COUNT_OF_PHOTOS_EXCEEDED("Count of photos exceeded");
    private final String description;

    ErrorType(String description) {
        this.description = description;
    }
}