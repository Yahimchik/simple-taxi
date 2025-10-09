package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.LoggedInUserId;
import com.simple.taxi.auth.service.UserService;
import com.simple.taxi.auth.service.VerificationTokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;

@RestController
@RequestMapping(ACTIVATION_CONTROLLER)
@RequiredArgsConstructor
public class ActivationController {
    private final UserService userService;
    private final VerificationTokenService verificationService;

    @GetMapping(DEACTIVATE)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<Void> deactivateUser(@LoggedInUserId UUID userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(REACTIVATE)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<Void> reactivateUser(@LoggedInUserId UUID userId) {
        userService.reactivateUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(RESEND_TOKEN)
    public ResponseEntity<Void> resendToken(@RequestParam String email) {
        verificationService.resendVerificationToken(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping(VERIFY)
    public ResponseEntity<Boolean> verify(@RequestParam("token") String token) {
        return ResponseEntity.ok(verificationService.verifyToken(token));
    }
}