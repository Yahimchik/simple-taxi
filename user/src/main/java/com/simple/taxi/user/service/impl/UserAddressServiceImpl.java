package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.model.dto.AddressRequestDTO;
import com.simple.taxi.user.model.entities.Address;
import com.simple.taxi.user.model.entities.UserAddress;
import com.simple.taxi.user.repository.AddressRepository;
import com.simple.taxi.user.repository.UserAddressRepository;
import com.simple.taxi.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final AddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
    private final OpenStreetMapGeocodingService openStreetMapGeocodingService;

    @Override
    public Flux<Address> getUserAddresses(UUID userId) {
        return userAddressRepository.findAllByUserId(userId)
                .flatMap(link -> addressRepository.findById(link.getAddressId()));
    }

    @Override
    public Mono<Void> addAddressToUser(UUID userId, AddressRequestDTO dto) {
        return openStreetMapGeocodingService.geocode(dto.getFormattedAddress(), dto.getLatitude(), dto.getLongitude())
                .flatMap(geo -> {
                    Address address = Address.builder()
                            .formattedAddress(dto.getFormattedAddress())
                            .street(geo.getStreet())
                            .houseNumber(geo.getHouseNumber())
                            .city(geo.getCity())
                            .region(geo.getRegion())
                            .country(geo.getCountry())
                            .postalCode(geo.getPostalCode())
                            .latitude(geo.getLatitude())
                            .longitude(geo.getLongitude())
                            .placeType(dto.getPlaceType())
                            .isActive(true)
                            .isVerified(true)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();

                    return addressRepository.findByFormattedAddress(address.getFormattedAddress())
                            .switchIfEmpty(addressRepository.save(address))
                            .flatMap(savedAddress -> userAddressRepository.save(new UserAddress(UUID.randomUUID(), userId, savedAddress.getId())));
                }).then();
    }

    @Override
    public Mono<Void> removeAddressFromUser(UUID userId, UUID addressId) {
        return userAddressRepository.deleteByUserIdAndAddressId(userId, addressId);
    }
}