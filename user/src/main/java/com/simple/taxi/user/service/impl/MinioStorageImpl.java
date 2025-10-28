package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.exception.FileException;
import com.simple.taxi.user.model.dto.FileResponse;
import com.simple.taxi.user.model.entities.UserFile;
import com.simple.taxi.user.model.enums.FileType;
import com.simple.taxi.user.repository.UserFileRepository;
import com.simple.taxi.user.service.FileStorage;
import com.simple.taxi.user.utils.FileValidator;
import com.simple.taxi.user.utils.StringGenerator;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.simple.taxi.user.model.enums.ErrorType.FAILED_TO_DELETE_FILE;
import static com.simple.taxi.user.model.enums.ErrorType.FAILED_TO_UPLOAD_FILE;
import static io.minio.http.Method.GET;
import static java.util.concurrent.TimeUnit.MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageImpl implements FileStorage {

    public static final int DURATION = 10;

    private final MinioClient minioClient;
    private final FileValidator fileValidator;
    private final UserFileRepository userFileRepository;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public Mono<FileResponse> addFile(FilePart file) {
        String originalFileName = file.filename();
        FileType fileType = fileValidator.determineFileType(file);

        return DataBufferUtils.join(file.content())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(buffer -> {
                    long fileSize = buffer.readableByteCount();
                    fileValidator.validateFileSize(fileType, fileSize);

                    return createUserFileDraft(originalFileName, fileType, file)
                            .flatMap(saved ->
                                    processAndUploadFile(file, saved, originalFileName, buffer, fileSize)
                                            .flatMap(this::buildFileResponse)
                            )
                            .doFinally(signal -> DataBufferUtils.release(buffer));
                });
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
                throw new FileException(FAILED_TO_DELETE_FILE);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<String> getUrl(String fileName) {
        return Mono.fromCallable(() -> minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(GET)
                        .bucket(bucket)
                        .object(fileName)
                        .expiry(DURATION, MINUTES)
                        .build()
        ));
    }

    private Mono<UserFile> createUserFileDraft(String originalFileName, FileType fileType, FilePart file) {
        UserFile userFile = UserFile.builder()
                .userId(UUID.fromString("019a15f3-a395-7150-b7f8-f351c397e45d")) // пример
                .originalFileName(originalFileName)
                .fileName("")
                .contentType(Objects.requireNonNull(file.headers().getContentType()).toString())
                .isDeleted(false)
                .fileType(fileType)
                .size(0L)
                .createdAt(Instant.now())
                .build();

        return userFileRepository.save(userFile);
    }

    private Mono<UserFile> processAndUploadFile(
            FilePart file,
            UserFile saved,
            String originalFileName,
            DataBuffer buffer,
            long fileSize
    ) {
        String fileName = StringGenerator.getUniqueNameOfFile(originalFileName, saved.getId());

        return uploadToMinio(fileName, file, buffer)
                .then(updateUserFileMetadata(saved, fileName, fileSize))
                .onErrorResume(error ->
                        deleteFile(fileName)
                                .then(userFileRepository.deleteById(saved.getId()))
                                .then(Mono.error(error))
                );
    }

    private Mono<Void> uploadToMinio(String fileName, FilePart file, DataBuffer buffer) {
        return Mono.fromRunnable(() -> {
            try (InputStream inputStream = buffer.asInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(fileName)
                                .stream(inputStream, buffer.readableByteCount(), -1)
                                .contentType(Objects.requireNonNull(file.headers().getContentType()).toString())
                                .build()
                );
            } catch (Exception e) {
                throw new FileException(FAILED_TO_UPLOAD_FILE);
            } finally {
                DataBufferUtils.release(buffer);
            }
        });
    }

    private Mono<UserFile> updateUserFileMetadata(UserFile saved, String fileName, long size) {
        saved.setFileName(fileName);
        saved.setSize(size);
        saved.setUpdatedAt(Instant.now());
        return userFileRepository.save(saved);
    }

    private Mono<FileResponse> buildFileResponse(UserFile file) {
        return getUrl(file.getFileName())
                .map(url -> new FileResponse(
                        file.getId(),
                        file.getFileName(),
                        file.getOriginalFileName(),
                        url
                ));
    }
}