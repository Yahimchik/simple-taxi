package com.simple.taxi.user.service.impl;

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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final int SIZE_MB = 10;
    private static final int PDF_FILE_SIZE_MB = 30;
    private final FileStorage storage;
    private final UserFileRepository userFileRepository;

    @Override
    public Mono<FileResponse> saveImageFile(FilePart file) {
        if (file.headers().getContentType() != null &&
                file.headers().getContentType().toString().startsWith("image")) {
            return storage.addFile(file); // уже возвращает FileResponse с URL
        }
        return Mono.error(new ValidationException(ErrorType.FILE_IMAGE_TYPE.getDescription()));
    }

    @Override
    public Mono<FileResponse> savePdfFile(FilePart file) {
        if (file.filename().toLowerCase().endsWith(".pdf")) {
            return storage.addFile(file); // уже возвращает FileResponse с URL
        }
        return Mono.error(new ValidationException(ErrorType.FILE_PDF_TYPE.getDescription()));
    }

    @Override
    public java.util.List<FileResponse> saveImageFiles(FilePart[] files) {
        throw new UnsupportedOperationException("Multiple file upload not yet implemented for reactive service");
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
    public void deleteById(UUID fileId) {
        // Реализация позже
    }
}