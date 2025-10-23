package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.model.dto.FileInfoDto;
import com.simple.taxi.user.model.dto.FileNameAndUUIDAtStorage;
import com.simple.taxi.user.model.dto.FileResponse;
import com.simple.taxi.user.model.enums.ErrorType;
import com.simple.taxi.user.service.FileService;
import com.simple.taxi.user.service.FileStorage;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private static final int SIZE_MB = 10;
    public static final int INITIAL_CAPACITY_OF_IMAGES = 6;
//    private final FileRepository repository;
    private final FileStorage storage;
    private static final int PDF_FILE_SIZE_MB = 30;

    @Override
    public FileResponse saveImageFile(MultipartFile file) {
        if (file.getSize() >= SIZE_MB * 1024 * 1024) {
//            throw new ValidationException(ErrorType.FILE_SIZE, SIZE_MB);
        }
        if (file.getContentType() != null) {
            String type = file.getContentType().split("/")[0];
            if ("image".equals(type)) {
                return fillFile(file);
            }
        }
//        throw new ValidationException(ErrorType.FILE_IMAGE_TYPE);
        return new FileResponse(UUID.randomUUID(), "");
    }

    @Override
    public FileResponse savePdfFile(MultipartFile file) {
        if (file.getSize() >= PDF_FILE_SIZE_MB * 1024 * 1024) {
//            throw new ValidationException(ErrorType.FILE_SIZE, PDF_FILE_SIZE_MB);
        }
        if (file.getContentType() != null) {
            String type = file.getOriginalFilename().split("\\.")[1];
            if ("pdf".equals(type)) {
                return fillFile(file);
            }
        }
//        throw new ValidationException(ErrorType.FILE_PDF_TYPE);
        return new FileResponse(UUID.randomUUID(), "");
    }

    private FileResponse fillFile(MultipartFile file) {
        FileNameAndUUIDAtStorage fileUUIDAndNameInStorage = storage.addFile(file);
        UUID uuidFile = fileUUIDAndNameInStorage.id();
//        FileEntity entity = new FileEntity();
//        entity.setId(fileUUIDAndNameInStorage.getUuid());
//        entity.setFileName(fileUUIDAndNameInStorage.getFileName());
//        entity.setCreatedAt(ZonedDateTime.now());
//        entity.setOriginalFileName(file.getOriginalFilename());
//        repository.save(entity);
        return new FileResponse(uuidFile, getInfo(uuidFile).fileName());
    }


    @Override
    public List<FileResponse> saveImageFiles(MultipartFile[] files) {
        List<FileResponse> images = new ArrayList<>(INITIAL_CAPACITY_OF_IMAGES);
        if (files.length <= INITIAL_CAPACITY_OF_IMAGES) {
            for (MultipartFile file : files) {
                images.add(saveImageFile(file));
            }
        } else {
            throw new ValidationException(ErrorType.COUNT_OF_PHOTOS_EXCEEDED.getDescription());
        }
        return images;
    }

    @Override
    public FileInfoDto getInfo(UUID idFile) {
//        FileEntity file = repository.getReferenceById(idFile);
        return new FileInfoDto(UUID.randomUUID(), "","");
    }

//    @Override
//    public FileEntity findFileById(final UUID fileId) {
//        return repository.findById(fileId)
//                .orElseThrow(() -> new ValidationException(FILE_NOT_FOUND));
//    }
//
//    @Override
//    public List<FileEntity> findFilesByIds(final Set<UUID> fileIds) throws ValidationException {
//        return repository.findAllById(fileIds);
//    }

    @Override
    public void deleteById(final UUID fileId) {
//        repository.deleteById(fileId);
    }


}
