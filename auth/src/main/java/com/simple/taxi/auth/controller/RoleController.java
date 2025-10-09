package com.simple.taxi.auth.controller;

import com.simple.taxi.auth.config.argument_resolver.LoggedInUserId;
import com.simple.taxi.auth.model.enums.RoleEnum;
import com.simple.taxi.auth.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

import static com.simple.taxi.auth.config.constant.UrlConstants.*;
import static com.simple.taxi.auth.config.constant.UrlConstants.GET_ROLES;

@RestController
@RequestMapping(ROLE_CONTROLLER)
@RequiredArgsConstructor
public class RoleController {

    private final UserService userService;

    @PostMapping(ASSIGN_ROLES)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<Void> assignRole(@LoggedInUserId UUID userid, @RequestParam RoleEnum role) {
        userService.assignRole(userid, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping(GET_ROLES)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<Set<RoleEnum>> getRoles(@LoggedInUserId UUID userId) {
        return ResponseEntity.ok(userService.getRoles(userId));
    }

    @DeleteMapping(REMOVE_ROLE)
    @SecurityRequirement(name = SECURITY_REQUIREMENT)
    public ResponseEntity<Void> removeRole(@LoggedInUserId UUID userid, @RequestParam RoleEnum role) {
        userService.removeRole(userid, role);
        return ResponseEntity.ok().build();
    }
}