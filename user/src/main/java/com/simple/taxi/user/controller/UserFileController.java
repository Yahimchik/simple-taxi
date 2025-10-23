package com.simple.taxi.user.controller;

import com.simple.taxi.user.service.FileService;
import com.simple.taxi.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserFileController {

    private final FileService fileStorageService;
    private final UserProfileService userProfileService;

    @PostMapping("/{userId}/avatar")
    public void uploadAvatar(@PathVariable UUID userId, @RequestParam MultipartFile file) {
        var objectName = fileStorageService.saveImageFile(file);
//        userProfileService.updateAvatar(userId, objectName.url());
    }
}