package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.LoggedInUserId;
import com.simple.taxi.auth.model.dto.RegistrationRequest;
import com.simple.taxi.auth.model.dto.UpdateUserDTO;
import com.simple.taxi.auth.model.dto.UserDTO;
import com.simple.taxi.auth.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;

@RestController
@RequestMapping(USER_CONTROLLER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(ID)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping(USER_CURRENT)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<UserDTO> getCurrent(@LoggedInUserId UUID userId) {
        return ResponseEntity.ok(userService.getCurrent(userId));
    }

    @PostMapping(USER_REGISTER)
    public ResponseEntity<UserDTO> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PutMapping(UPDATE_USER)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<UserDTO> updateUser(@LoggedInUserId UUID userId, @RequestBody UpdateUserDTO user) {
        return ResponseEntity.ok(userService.updateUser(userId, user));
    }
}