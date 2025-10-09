package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.ClientIp;
import com.simple.taxi.auth.config.argument_resolver.DeviceInfo;
import com.simple.taxi.auth.config.argument_resolver.LoggedInUserId;
import com.simple.taxi.auth.model.dto.AuthRequest;
import com.simple.taxi.auth.model.dto.LoginRequest;
import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.model.enums.LoginType;
import com.simple.taxi.auth.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;

@Slf4j
@RestController
@RequestMapping(AUTH_CONTROLLER)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping(CLIENT_ID)
    public ResponseEntity<String> getClientId(@RequestParam LoginType loginType) {
        return ResponseEntity.ok(authService.getClientId(loginType));
    }

    @PostMapping(LOGIN)
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest request,
            @ClientIp String clientIp,
            @DeviceInfo String deviceInfo) {
        return ResponseEntity.ok(authService.login(request, clientIp, deviceInfo));
    }

    @PostMapping(LOGIN_SOCIAL)
    public ResponseEntity<TokenResponse> loginSocial(
            @RequestBody AuthRequest request,
            @RequestParam LoginType loginType,
            @ClientIp String clientIp,
            @DeviceInfo String deviceInfo) {
        return ResponseEntity.ok(authService.loginWithSocial(request, loginType, clientIp, deviceInfo));
    }

    @PostMapping(LOGOUT)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<Void> logout(
            @LoggedInUserId UUID userId,
            @RequestParam String deviceId) {
        authService.logout(userId, deviceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(LOGOUT_ALL)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<Void> logoutAll(@LoggedInUserId UUID userId) {
        authService.logoutAll(userId);
        return ResponseEntity.noContent().build();
    }
}