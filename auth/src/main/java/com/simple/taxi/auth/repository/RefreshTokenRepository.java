package com.simple.taxi.auth.repository;

import com.simple.taxi.auth.model.entity.RefreshToken;
import com.simple.taxi.auth.model.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserDeviceAndRevokedFalse(UserDevice userDevice);

    Optional<RefreshToken> findByUserDeviceAndRevokedFalse(UserDevice device);
}