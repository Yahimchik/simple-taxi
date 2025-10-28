package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.FileResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileStorage {
    Mono<FileResponse> addFile(FilePart file);

    Mono<String> getUrl(String fileName);

    Mono<Void> deleteFile(String fileName);
}