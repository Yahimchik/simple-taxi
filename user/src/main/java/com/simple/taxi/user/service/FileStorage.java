package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.FileResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface FileStorage {
    Mono<FileResponse> addFile(FilePart file);

    Mono<String> getUrl(String fileName);

    Mono<Void> deleteFile(String fileName);

    default String getUniqueNameOfFile(String originalFileName, UUID uuid) {
        return LocalDate.now() + "/" + uuid + "." + FilenameUtils.getExtension(originalFileName);
    }
}