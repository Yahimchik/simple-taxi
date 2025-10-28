package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.exception.FileException;
import com.simple.taxi.user.exception.NotFoundException;
import com.simple.taxi.user.model.dto.FileResponse;
import com.simple.taxi.user.model.enums.ErrorType;
import com.simple.taxi.user.repository.UserFileRepository;
import com.simple.taxi.user.service.FileService;
import com.simple.taxi.user.service.FileStorage;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.simple.taxi.user.model.enums.ErrorType.FILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileStorage storage;
    private final UserFileRepository userFileRepository;

    @Override
    public Mono<FileResponse> saveImageFile(FilePart file) {
        if (file.headers().getContentType() != null &&
                Objects.requireNonNull(file.headers().getContentType()).toString().startsWith("image")) {
            return storage.addFile(file);
        }
        return Mono.error(new ValidationException(ErrorType.FILE_IMAGE_TYPE.getDescription()));
    }

    @Override
    public Mono<FileResponse> savePdfFile(FilePart file) {
        if (file.filename().toLowerCase().endsWith(".pdf")) {
            return storage.addFile(file);
        }
        return Mono.error(new ValidationException(ErrorType.FILE_PDF_TYPE.getDescription()));
    }

    @Override
    public Mono<FileResponse> getInfo(UUID fileId) {
        return userFileRepository.findById(fileId)
                .flatMap(file ->
                        storage.getUrl(file.getFileName())
                                .map(url -> new FileResponse(
                                        file.getId(),
                                        file.getFileName(),
                                        file.getOriginalFileName(),
                                        url
                                ))
                );
    }

    @Override
    public Mono<Void> deleteById(UUID fileId) {
        return userFileRepository.findById(fileId)
                .switchIfEmpty(Mono.error(new NotFoundException(FILE_NOT_FOUND, fileId)))
                .flatMap(file -> {
                    if (file.getIsDeleted()) return Mono.error(new FileException(FILE_NOT_FOUND));

                    file.setIsDeleted(true);
                    file.setUpdatedAt(Instant.now());
                    file.setDeletedAt(Instant.now());
                    return userFileRepository.save(file).then();
                });
    }
}