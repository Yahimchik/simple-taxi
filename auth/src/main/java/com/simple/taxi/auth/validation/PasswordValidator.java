package com.simple.taxi.auth.validation;

import com.simple.taxi.auth.exception.ValidationException;
import com.simple.taxi.auth.model.enums.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.simple.taxi.auth.model.enums.ErrorType.*;

@Component
@RequiredArgsConstructor
public class PasswordValidator {
    private final PasswordEncoder passwordEncoder;

    public void validatePassword(String oldPassword, String passwordHash, ErrorType errorType) {
        if (!passwordEncoder.matches(oldPassword, passwordHash)) {
            throw new ValidationException(errorType);
        }
    }

    public void validateNewPassword(String newPassword, String oldPasswordHash) {
        if (passwordEncoder.matches(newPassword, oldPasswordHash)) {
            throw new ValidationException(NEW_PASSWORD_EQUALS_TO_OLD);
        }
    }

    public String generatePasswordFromEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException(USER_EMAIL_INVALID);
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(email.getBytes(StandardCharsets.UTF_8));

            StringBuilder passwordBuilder = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                passwordBuilder.append(String.format("%02x", hash[i]));
            }

            return passwordBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 недоступен", e);
        }
    }

    public void validateConfirmPassword(String confirmPassword, String passwordHash) {
        if (!passwordEncoder.matches(confirmPassword, passwordHash)) {
            throw new ValidationException(USER_CONFIRM_PASSWORD_NOT_MATCH);
        }
    }
}