package com.simple.taxi.auth.service;

import com.simple.taxi.auth.model.dto.RegistrationRequest;
import com.simple.taxi.auth.model.dto.UpdateUserDTO;
import com.simple.taxi.auth.model.dto.UserDTO;
import com.simple.taxi.auth.model.dto.UserDeviceDTO;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.enums.RoleEnum;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    UserDTO register(RegistrationRequest request);

    void forgotPassword(String email);

    void resetPassword(String tokenValue, String newPassword, String ip);

    void changePassword(UUID userId, String oldPassword, String newPassword);

    UserDTO getUser(UUID id);

    UserDTO getCurrent(UUID userId);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(UUID id, UpdateUserDTO userDTO);

    void deactivateUser(UUID id);

    void reactivateUser(UUID userId);

    void assignRole(UUID userId, RoleEnum roleEnum);

    void removeRole(UUID userId, RoleEnum roleEnum);

    Set<RoleEnum> getRoles(UUID userId);
}