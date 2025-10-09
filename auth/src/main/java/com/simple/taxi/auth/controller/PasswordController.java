package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.ClientIp;
import com.simple.taxi.auth.config.argument_resolver.LoggedInUserId;
import com.simple.taxi.auth.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;

@RestController
@RequestMapping(PASSWORD_CONTROLLER)
@RequiredArgsConstructor
public class PasswordController {

    private final UserService userService;

    @PostMapping(CHANGE_PASSWORD)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<Void> changePassword(@LoggedInUserId UUID userId,
                                               @RequestParam String oldPassword,
                                               @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(FORGOT)
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(RESET_PASSWORD)
    public ResponseEntity<Void> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @ClientIp String clientIp
    ) {
        userService.resetPassword(token, newPassword, clientIp);
        return ResponseEntity.noContent().build();
    }
}