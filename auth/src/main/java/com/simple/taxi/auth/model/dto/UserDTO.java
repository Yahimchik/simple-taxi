package com.simple.taxi.auth.model.dto;

import com.simple.taxi.auth.model.enums.RoleEnum;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String email;
    private String phone;
    private String status;
    private Set<RoleEnum> roles;
    private Set<AuthUserDTO> authUsers;
    private Set<UserDeviceDTO> userDevices;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean twoFactorEnabled;
}