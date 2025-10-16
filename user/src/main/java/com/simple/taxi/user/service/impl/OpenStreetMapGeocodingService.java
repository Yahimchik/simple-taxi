package com.simple.taxi.user.service.impl;

import com.simple.taxi.user.model.dto.GeoResult;
import com.simple.taxi.user.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenStreetMapGeocodingService implements GeocodingService {

    // Публичный Nominatim сервер
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .build();

    @Override
    public Mono<GeoResult> geocode(String formattedAddress, Double latitude, Double longitude) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", formattedAddress)
                        .queryParam("format", "json")
                        .queryParam("addressdetails", 1) // получить подробности адреса
                        .build())
                .header("User-Agent", "spring-app-geocoder") // Nominatim требует User-Agent
                .retrieve()
                .bodyToMono(List.class)
                .map(list -> toGeoResult(list, formattedAddress));
    }

    @SuppressWarnings("unchecked")
    private GeoResult toGeoResult(List<Map<String, Object>> response, String fallbackAddress) {
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Адрес не найден");
        }

        Map<String, Object> firstResult = response.get(0);

        double lat = Double.parseDouble((String) firstResult.get("lat"));
        double lon = Double.parseDouble((String) firstResult.get("lon"));

        Map<String, Object> address = (Map<String, Object>) firstResult.get("address");

        String country = (String) address.get("country");
        String city = address.containsKey("city") ? (String) address.get("city") :
                address.containsKey("town") ? (String) address.get("town") :
                        address.containsKey("village") ? (String) address.get("village") : null;

        String street = (String) address.get("road");
        String house = (String) address.get("house_number");
        String postalCode = (String) address.get("postcode");

        return GeoResult.builder()
                .street(street)
                .houseNumber(house)
                .city(city != null ? city : fallbackAddress)
                .region((String) address.get("state"))
                .country(country)
                .postalCode(postalCode)
                .latitude(lat)
                .longitude(lon)
                .placeId((String) firstResult.get("osm_id").toString())
                .build();
    }
}
