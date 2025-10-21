package org.example.geo.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoResult {
    private String street;
    private String houseNumber;
    private String city;
    private String region;
    private String country;
    private String postalCode;
    private String placeId;      // идентификатор в API карт (Google Place ID, Yandex ID)
    private Double latitude;
    private Double longitude;
}
