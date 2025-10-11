package com.simple.taxi.user.repository;

import com.simple.taxi.user.model.entities.UserProfile;
import lombok.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, UUID> {

    Mono<UserProfile> findByEmail(String email);

    Mono<UserProfile> findByPhone(String phone);

    Flux<UserProfile> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String q1, String q2);
}