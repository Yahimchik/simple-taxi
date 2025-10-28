package com.simple.taxi.user.utils;

import com.simple.taxi.user.model.enums.FileType;
import org.springframework.http.codec.multipart.FilePart;

public interface FileValidator {
    FileType determineFileType(FilePart file);

    void validateFileSize(FileType type, long size);
}