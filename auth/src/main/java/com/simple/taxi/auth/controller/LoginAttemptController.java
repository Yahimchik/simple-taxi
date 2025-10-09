package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.ClientIp;
import com.simple.taxi.auth.model.enums.BlockType;
import com.simple.taxi.auth.service.LoginAttemptService;
import com.simple.taxi.auth.service.impl.LoginAttemptServiceImpl;
import com.simple.taxi.auth.service.impl.LoginAttemptServiceImpl.AttemptStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;

@RestController
@RequestMapping(LOGIN_ATTEMPTS_CONTROLLER)
@RequiredArgsConstructor
public class LoginAttemptController {

    private final LoginAttemptService loginAttemptService;

    @PostMapping(BLOCK_TIME)
    public ResponseEntity<String> getBlockTime(@ClientIp String clientIp) {
        String remaining = loginAttemptService.getBlockRemainingReadable(clientIp);
        return ResponseEntity.ok(remaining != null ? remaining : "Not blocked");
    }

    @GetMapping(STATUS)
    public ResponseEntity<AttemptStatus> getStatus(
            @RequestParam String email,
            @RequestParam String deviceId,
            @ClientIp String clientIp) {
        return ResponseEntity.ok(loginAttemptService.getStatus(email, deviceId, clientIp));
    }

    @GetMapping(CLEAR)
    public ResponseEntity<Boolean> clearAttempts(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String deviceId,
            @ClientIp String clientIp) {
        loginAttemptService.clearAttempts(email, deviceId, clientIp);
        return ResponseEntity.ok().build();
    }
}