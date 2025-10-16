package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.model.dto.GeoResult;
import com.simple.taxi.user.model.dto.YandexGeocodeResponse;
import com.simple.taxi.user.service.GeocodingService;
import com.simple.taxi.user.service.YandexIamTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class YandexGeocodingService implements GeocodingService {

    private final WebClient webClient;
    private final YandexIamTokenProvider tokenProvider;

    @Override
    public Mono<GeoResult> geocode(String formattedAddress, Double latitude, Double longitude) {
        return tokenProvider.getToken()
                .flatMap(token -> webClient.get()
                        .uri("https://geocode-maps.yandex.ru/1.x/?format=json&geocode={address}", formattedAddress)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(YandexGeocodeResponse.class)
                        .map(this::toGeoResult));
    }

    private GeoResult toGeoResult(YandexGeocodeResponse response) {
        var geoObject = response.getResponse()
                .getGeoObjectCollection()
                .getFeatureMember()
                .get(0)
                .getGeoObject();

        String[] coords = geoObject.getPos().getPos().split(" ");
        double lon = Double.parseDouble(coords[0]);
        double lat = Double.parseDouble(coords[1]);

        YandexGeocodeResponse.Thoroughfare thoroughfare = geoObject.getMetaDataProperty()
                .getGeocoderMetaData()
                .getAddressDetails()
                .getCountry()
                .getAdministrativeArea()
                .getLocality()
                .getThoroughfare();

        String street = thoroughfare != null ? thoroughfare.getThoroughfareName() : null;
        String houseNumber = thoroughfare != null && thoroughfare.getPremise() != null ? thoroughfare.getPremise().getPremiseNumber() : null;
        String city = geoObject.getMetaDataProperty().getGeocoderMetaData().getAddressDetails()
                .getCountry().getAdministrativeArea().getLocality().getLocalityName();

        return GeoResult.builder()
                .street(street)
                .houseNumber(houseNumber)
                .city(city)
                .region(null)  // можно достать при необходимости
                .country("Россия")
                .postalCode(null)
                .latitude(lat)
                .longitude(lon)
                .placeId(null) // Яндекс Place ID можно не использовать
                .build();
    }
}

