package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.FileNameAndUUIDAtStorage;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

public interface FileStorage {
    FileNameAndUUIDAtStorage addFile(MultipartFile file);

    String getActualUrl(String fileName);

    void deleteFile(String fileName);

    default String getUniqueNameOfFile(String originalFileName, UUID uuid) {
        return LocalDate.now() + "/" + uuid + "." + FilenameUtils.getExtension(originalFileName);
    }
}