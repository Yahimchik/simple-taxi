package com.simple.taxi.auth.repository;

import com.simple.taxi.auth.model.entity.User;
import com.simple.taxi.auth.model.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<List<UserDevice>> findByUserId(UUID userId);

    Optional<UserDevice> findByUserIdAndDeviceId(UUID userId, String deviceId);

    List<UserDevice> findUserDeviceByUser(User user);

}