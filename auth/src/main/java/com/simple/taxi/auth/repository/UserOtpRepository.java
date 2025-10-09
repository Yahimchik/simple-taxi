package com.simple.taxi.auth.repository;

import com.simple.taxi.auth.model.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserOtpRepository extends JpaRepository<UserOtp, UUID> {
    Optional<UserOtp> findByUser_EmailAndCodeAndUsedFalse(String email, String code);

    Optional<UserOtp> findByUserIdAndCodeAndUsedFalse(UUID userId, String code);
}