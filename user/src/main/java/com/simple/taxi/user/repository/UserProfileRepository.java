package com.simple.taxi.user.repository;

import com.simple.taxi.user.model.entities.UserProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, UUID> {

}
