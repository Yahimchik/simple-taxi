package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.model.dto.FileNameAndUUIDAtStorage;
import com.simple.taxi.user.service.FileStorage;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageImpl implements FileStorage {
    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucket;
    @Value("${minio.url}")
    private String endpoint;

    @Override
    public FileNameAndUUIDAtStorage addFile(MultipartFile file) {
        UUID uuid = UUID.randomUUID();
        String fileName = getUniqueNameOfFile(file.getOriginalFilename(), uuid);
        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(fileName)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), 0)
                    .build());

            return new FileNameAndUUIDAtStorage(uuid, fileName);
        } catch (Exception e) {
            log.warn("Exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getActualUrl(String fileName) {
        return endpoint + "/" + bucket + "/" + fileName;
    }

    @Override
    public void deleteFile(String fileName) {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs
                .builder()
                .bucket(bucket)
                .object(fileName)
                .build();
        log.debug("Remove objected was created!");

        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Object has deleted from minio storage!");
    }
}