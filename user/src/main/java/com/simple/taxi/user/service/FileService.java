package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.FileResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface FileService {
    Mono<FileResponse> saveImageFile(FilePart file);

    Mono<FileResponse> savePdfFile(FilePart file);

    List<FileResponse> saveImageFiles(FilePart[] files);

    Mono<FileResponse> getInfo(UUID idFile);

//    FileEntity findFileById(final UUID fileId);
//
//    List<FileEntity> findFilesByIds(final Set<UUID> fileId);

    void deleteById(final UUID fileId);
}
