package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.AddressRequestDTO;
import com.simple.taxi.user.model.entities.Address;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserAddressService {
    Flux<Address> getUserAddresses(UUID userId);
    Mono<Void> addAddressToUser(UUID userId, AddressRequestDTO dto);
    Mono<Void> removeAddressFromUser(UUID userId, UUID addressId);
}
