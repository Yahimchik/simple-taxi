package com.simple.taxi.user.repository;

import com.simple.taxi.user.model.entities.UserAddress;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserAddressRepository extends ReactiveCrudRepository<UserAddress, UUID> {
    Flux<UserAddress> findAllByUserId(UUID userId);
    Flux<UserAddress> findAllByAddressId(UUID addressId);
    Mono<Void> deleteByUserIdAndAddressId(UUID userId, UUID addressId);
}