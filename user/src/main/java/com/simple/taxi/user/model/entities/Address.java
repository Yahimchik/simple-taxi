package com.simple.taxi.user.model.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("addresses")
public class Address {

    @Id
    private UUID id;

    private String formattedAddress;
    private String street;
    private String houseNumber;
    private String city;
    private String region;
    private String country;
    private String postalCode;

    private Double latitude;
    private Double longitude;

    private String placeType; // например: AIRPORT, METRO, HOME, OFFICE, HOTEL
    private String zoneCode;  // код района или тарифной зоны
    private String mapProviderId; // идентификатор в API (например, Google Place ID)

    private Boolean isVerified; // прошёл ли валидацию геокодера
    private Boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
}