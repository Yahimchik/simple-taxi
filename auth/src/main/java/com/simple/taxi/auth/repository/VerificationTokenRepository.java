package com.simple.taxi.auth.repository;

import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUserAndUsedIsFalse(User user);

    void deleteByUser(User user);

    void deleteByUserId(UUID userId);

    Optional<VerificationToken> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}