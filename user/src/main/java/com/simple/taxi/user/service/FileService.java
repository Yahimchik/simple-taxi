package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.FileInfoDto;
import com.simple.taxi.user.model.dto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FileService {
    FileResponse saveImageFile(MultipartFile file);

    FileResponse savePdfFile(MultipartFile file);

    List<FileResponse> saveImageFiles(MultipartFile[] files);

    FileInfoDto getInfo(UUID idFile);

//    FileEntity findFileById(final UUID fileId);
//
//    List<FileEntity> findFilesByIds(final Set<UUID> fileId);

    void deleteById (final UUID fileId);
}
