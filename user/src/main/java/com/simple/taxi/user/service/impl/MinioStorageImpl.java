package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.model.dto.FileResponse;
import com.simple.taxi.user.model.entities.UserFile;
import com.simple.taxi.user.model.enums.FileType;
import com.simple.taxi.user.repository.UserFileRepository;
import com.simple.taxi.user.service.FileStorage;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageImpl implements FileStorage {

    private final MinioClient minioClient;
    private final UserFileRepository userFileRepository;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String endpoint;

    @Override
    public Mono<FileResponse> addFile(FilePart file) {
        String originalFileName = file.filename();

        String mime = file.headers().getContentType() != null
                ? file.headers().getContentType().toString()
                : "";
        FileType fileType;
        if (mime.startsWith("image/")) {
            fileType = FileType.PHOTO;
        } else if (mime.equals("application/pdf")) {
            fileType = FileType.DOCUMENT;
        } else {
            return Mono.error(new ValidationException("Unsupported file type: " + mime));
        }

        UserFile userFile = UserFile.builder()
                .userId(UUID.fromString("019a15f3-a395-7150-b7f8-f351c397e45d"))
                .originalFileName(originalFileName)
                .fileName("")
                .contentType(file.headers().getContentType().toString())
                .isDeleted(false)
                .fileType(fileType)
                .size(0L)
                .createdAt(Instant.now())
                .build();

        return userFileRepository.save(userFile) // Сначала сохраняем в БД
                .flatMap(saved -> {
                    String fileName = getUniqueNameOfFile(originalFileName, saved.getId());

                    return DataBufferUtils.join(file.content())
                            .publishOn(Schedulers.boundedElastic())
                            .flatMap(dataBuffer -> {

                                long fileSize = dataBuffer.readableByteCount(); // размер в байтах

                                // Проверка размера
                                if (fileType == FileType.PHOTO && fileSize > 5 * 1024 * 1024) {
                                    DataBufferUtils.release(dataBuffer);
                                    return Mono.error(new ValidationException("Image file is too large (max 5MB)"));
                                } else if (fileType == FileType.DOCUMENT && fileSize > 30 * 1024 * 1024) {
                                    DataBufferUtils.release(dataBuffer);
                                    return Mono.error(new ValidationException("PDF file is too large (max 30MB)"));
                                }

                                try (InputStream inputStream = dataBuffer.asInputStream()) {
                                    minioClient.putObject(
                                            PutObjectArgs.builder()
                                                    .bucket(bucket)
                                                    .object(fileName)
                                                    .stream(inputStream, dataBuffer.readableByteCount(), -1)
                                                    .contentType(file.headers().getContentType().toString())
                                                    .build()
                                    );
                                    DataBufferUtils.release(dataBuffer);

                                    // Обновляем запись с реальным именем файла и временем обновления
                                    saved.setFileName(fileName);
                                    saved.setSize(fileSize);
                                    saved.setUpdatedAt(Instant.now());

                                    return userFileRepository.save(saved)
                                            .flatMap(updated ->
                                                    getUrl(fileName)
                                                            .map(url -> new FileResponse(
                                                                    updated.getId(),
                                                                    updated.getFileName(),
                                                                    updated.getOriginalFileName(),
                                                                    url
                                                            ))
                                            )
                                            .onErrorResume(dbError ->
                                                    deleteFile(fileName)
                                                            .then(userFileRepository.deleteById(userFile.getId()))
                                                            .then(Mono.error(dbError))
                                            );
                                } catch (Exception e) {
                                    DataBufferUtils.release(dataBuffer);
                                    return Mono.error(e);
                                }
                            });
                });
    }

    @Override
    public Mono<String> getUrl(String fileName) {
        return Mono.fromCallable(() -> minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(fileName)
                        .expiry(10, TimeUnit.MINUTES)
                        .build()
        ));
    }

    @Override
    public Mono<Void> deleteFile(String fileName) {
        return Mono.fromRunnable(() -> {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucket)
                                .object(fileName)
                                .build()
                );
            } catch (Exception e) {
                log.error("Failed to delete file from MinIO", e);
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}