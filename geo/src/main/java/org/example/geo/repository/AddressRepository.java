package org.example.geo.repository;

import org.example.geo.model.entities.Address;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressRepository extends ReactiveCrudRepository<Address, UUID> {
    Mono<Address> findByFormattedAddress(String formattedAddress);
}
