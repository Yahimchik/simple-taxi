package com.simple.taxi.user.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO {
    private String label;             // "Дом", "Работа" — хранить можно в UserAddress
    private String formattedAddress;  // обязательное для сохранения
    private Double latitude;          // для маршрутов
    private Double longitude;         // для маршрутов
    private String placeType;         // HOME, OFFICE, AIRPORT
}

