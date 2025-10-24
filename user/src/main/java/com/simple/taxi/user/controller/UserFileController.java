package com.simple.taxi.user.controller;

import com.simple.taxi.user.model.dto.FileResponse;
import com.simple.taxi.user.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserFileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Загрузить файл",
            description = "Загружает изображение или PDF в MinIO",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "multipart/form-data"

                    )
            )
    )
    public Mono<FileResponse> uploadFile(@RequestPart("file") FilePart file) {
        return fileService.saveImageFile(file);
    }
}
