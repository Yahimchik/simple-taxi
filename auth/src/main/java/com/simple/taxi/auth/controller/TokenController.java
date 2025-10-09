package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.DeviceInfo;
import com.simple.taxi.auth.config.argument_resolver.LoggedInUserId;
import com.simple.taxi.auth.model.dto.RefreshTokenDTO;
import com.simple.taxi.auth.model.dto.RefreshTokenRequest;
import com.simple.taxi.auth.model.dto.TokenResponse;
import com.simple.taxi.auth.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;

@RestController
@RequestMapping(TOKEN_CONTROLLER)
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping(ACTIVE_TOKEN)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<List<RefreshTokenDTO>> getActiveTokens(@LoggedInUserId UUID userId) {
        return ResponseEntity.ok(tokenService.getActiveTokens(userId));
    }

    @GetMapping(CHECK_TOKEN)
    public ResponseEntity<Boolean> checkToken(@RequestParam String token) {
        return ResponseEntity.ok(tokenService.checkToken(token));
    }

    @PostMapping(RECREATE_TOKEN)
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request, @DeviceInfo String deviceInfo) {
        return ResponseEntity.ok(tokenService.refreshToken(request,deviceInfo));
    }
}