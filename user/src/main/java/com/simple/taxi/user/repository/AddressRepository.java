package com.simple.taxi.user.repository;

import com.simple.taxi.user.model.entities.Address;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressRepository extends ReactiveCrudRepository<Address, UUID> {
    Mono<Address> findByFormattedAddress(String formattedAddress);
}
