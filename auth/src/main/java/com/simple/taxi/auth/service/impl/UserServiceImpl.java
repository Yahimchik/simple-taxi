package com.simple.taxi.auth.service.impl;

import com.simple.taxi.auth.exception.NotFoundException;
import com.simple.taxi.auth.exception.ValidationException;
import com.simple.taxi.auth.kafka.EventProducer;
import com.simple.taxi.auth.mapper.UserMapper;
import com.simple.taxi.auth.model.dto.LoginContext;
import com.simple.taxi.auth.model.dto.RegistrationRequest;
import com.simple.taxi.auth.model.dto.UpdateUserDTO;
import com.simple.taxi.auth.model.dto.UserDTO;
import com.simple.taxi.auth.model.entity.Role;
import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.VerificationToken;
import com.simple.taxi.auth.model.enums.RoleEnum;
import com.simple.taxi.auth.repository.RoleRepository;
import com.simple.taxi.auth.repository.UserRepository;
import com.simple.taxi.auth.repository.VerificationTokenRepository;
import com.simple.taxi.auth.service.*;
import com.simple.taxi.auth.util.UuidGenerator;
import com.simple.taxi.auth.util.VerificationTokenManager;
import com.simple.taxi.auth.validation.PasswordValidator;
import com.simple.taxi.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.simple.taxi.auth.model.enums.ErrorType.*;
import static com.simple.taxi.auth.model.enums.RoleEnum.USER;
import static com.simple.taxi.auth.model.enums.Status.*;
import static com.simple.taxi.auth.model.enums.Type.RESET_PASSWORD;
import static com.simple.taxi.dto.NotificationChannel.EMAIL;
import static com.simple.taxi.dto.NotificationType.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenService verificationService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationTokenManager verificationTokenManager;
    private final PasswordEncoder passwordEncoder;
    private final UuidGenerator uuidGenerator;
    private final PasswordValidator passwordValidator;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final LoginAttemptService loginAttemptService;
    private final TokenService tokenService;
    private final EventProducer eventProducer;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public UserDTO register(RegistrationRequest request) {

        if (userRepository.existsByEmail(request.email())) throw new ValidationException(USER_EMAIL_EXISTS);
        if (userRepository.existsByPhone(request.phone())) throw new ValidationException(USER_PHONE_EXISTS);

        var role = roleRepository.findByName(USER)
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));

        User user = User.builder()
                .id(uuidGenerator.generateV7())
                .email(request.email())
                .phone(request.phone())
                .passwordHash(passwordEncoder.encode(request.password()))
                .roles(Set.of(role))
                .status(PENDING)
                .build();

        User savedUser = userRepository.save(user);
        var token = verificationService.createVerificationToken(savedUser);

        notificationService.sendEmail(user.getId(), user.getEmail(), REGISTRATION_CONFIRMATION, Map.of("token", token.getToken()));
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        VerificationToken token = verificationTokenManager.createVerificationToken(user, RESET_PASSWORD, false);

        notificationService.sendEmail(user.getId(), user.getEmail(), PASSWORD_RESET, Map.of("token", token.getToken()));
        verificationTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void resetPassword(String tokenValue, String newPassword, String ip) {
        VerificationToken token = verificationService.findVerificationTokenByTokenValue(tokenValue);
        verificationTokenManager.isTokenExpired(token);

        User user = token.getUser();
        passwordValidator.validateNewPassword(newPassword, user.getPasswordHash());

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        verificationTokenRepository.save(token);

        user.getUserDevices()
                .forEach(device -> {
                    LoginContext ctx = new LoginContext(user.getEmail(), device.getDeviceId(), device.getDeviceInfo(), ip);
                    loginAttemptService.loginSucceeded(ctx);
                });
        tokenService.revokeAllTokens(user.getId());
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new ValidationException(USER_CONFIRM_PASSWORD_NOT_MATCH);
        }

        passwordValidator.validateNewPassword(newPassword, user.getPasswordHash());

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        notificationService.sendEmail(user.getId(), user.getEmail(), PASSWORD_CHANGED, null);

        userRepository.save(user);
        tokenService.revokeAllTokens(userId);
    }

    @Override
    public UserDTO getUser(UUID id) {
        return userMapper.toDto(findUser(id));
    }

    @Override
    public UserDTO getCurrent(UUID userId) {
        return userMapper.toDto(findUser(userId));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(UUID id, UpdateUserDTO userToUpdate) {
        User user = findUser(id);
        if (userToUpdate.email() != null) user.setEmail(userToUpdate.email());
        if (userToUpdate.phoneNumber() != null) user.setPhone(userToUpdate.phoneNumber());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        User user = findUser(id);
        user.setStatus(DEACTIVATED);
        userRepository.save(user);
    }

    @Override
    public void reactivateUser(UUID userId) {
        User user = findUser(userId);
        verificationTokenManager.isActive(user);
        user.setStatus(ACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignRole(UUID userId, RoleEnum roleEnum) {
        User user = findUser(userId);
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRole(UUID userId, RoleEnum roleEnum) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        boolean removed = user.getRoles()
                .removeIf(role -> role.getName().equals(roleEnum));

        if (!removed) {
            throw new NotFoundException(NO_SUCH_ROLE_AT_USER);
        }

        userRepository.save(user);
    }

    @Override
    public Set<RoleEnum> getRoles(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }
}