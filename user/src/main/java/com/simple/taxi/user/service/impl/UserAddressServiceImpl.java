package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.model.dto.AddressRequestDTO;
import com.simple.taxi.user.model.entities.Address;
import com.simple.taxi.user.repository.AddressRepository;
import com.simple.taxi.user.repository.UserAddressRepository;
import com.simple.taxi.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final AddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;

    @Override
    public Flux<Address> getUserAddresses(UUID userId) {
        return userAddressRepository.findAllByUserId(userId)
                .flatMap(link -> addressRepository.findById(link.getAddressId()));
    }

    @Override
    public Mono<Void> addAddressToUser(UUID userId, AddressRequestDTO dto) {
       return Mono.empty();
    }

    @Override
    public Mono<Void> removeAddressFromUser(UUID userId, UUID addressId) {
        return userAddressRepository.deleteByUserIdAndAddressId(userId, addressId);
    }
}