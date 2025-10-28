package com.simple.taxi.user.utils.impl;

import com.simple.taxi.user.exception.FileException;
import com.simple.taxi.user.model.enums.ErrorType;
import com.simple.taxi.user.model.enums.FileType;
import com.simple.taxi.user.utils.FileValidator;
import jakarta.validation.ValidationException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.simple.taxi.user.model.enums.ErrorType.FILE_IS_TO_LARGE;
import static com.simple.taxi.user.model.enums.FileType.DOCUMENT;
import static com.simple.taxi.user.model.enums.FileType.PHOTO;

@Component
public class FileValidatorImpl implements FileValidator {

    public static final int IMAGE_MAX_SIZE = 5 * 1024 * 1024;
    public static final int DOCUMENT_MAX_SIZE = 30 * 1024 * 1024;

    public static final String IMAGE_TYPE = "image/";
    public static final String DOCUMENT_TYPE = "application/pdf";
    public static final String MAX_SIZE_FOR_IMAGE = "Max size for image – 5MB";
    public static final String MAX_SIZE_FOR_DOCUMENT = "Max size for document – 30MB";

    @Override
    public FileType determineFileType(FilePart file) {
        String mime = file.headers().getContentType() != null
                ? Objects.requireNonNull(file.headers().getContentType()).toString()
                : "";

        if (mime.startsWith(IMAGE_TYPE)) return PHOTO;
        if (mime.equals(DOCUMENT_TYPE)) return DOCUMENT;
        throw new ValidationException("Unsupported file type: " + mime);
    }

    @Override
    public void validateFileSize(FileType type, long size) {
        if (type == PHOTO && size > IMAGE_MAX_SIZE) throw new FileException(FILE_IS_TO_LARGE, MAX_SIZE_FOR_IMAGE);
        else if (type == DOCUMENT && size > DOCUMENT_MAX_SIZE) throw new FileException(FILE_IS_TO_LARGE, MAX_SIZE_FOR_DOCUMENT);
    }
}