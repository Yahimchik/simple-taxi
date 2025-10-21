package org.example.geo.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.geo.model.dto.GeoResult;
import org.example.geo.service.GeocodingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class YandexGeocodingService implements GeocodingService {

    @Value("${yandex.geocoder.api-key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://geocode-maps.yandex.ru/1.x/")
            .build();

    @Override
    public Mono<List<GeoResult>> geocode(String formattedAddress, Double latitude, Double longitude) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("geocode", formattedAddress)
                        .queryParam("format", "json")
                        .queryParam("lang", "ru_RU")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::toGeoResults);
    }

    @SuppressWarnings("unchecked")
    private List<GeoResult> toGeoResults(Map<String, Object> response) {
        Map<String, Object> responseObj = (Map<String, Object>) response.get("response");
        if (responseObj == null)
            throw new RuntimeException("Некорректный ответ от Yandex Geocoder API");

        Map<String, Object> geoCollection = (Map<String, Object>) responseObj.get("GeoObjectCollection");
        List<Map<String, Object>> featureMembers = (List<Map<String, Object>>) geoCollection.get("featureMember");

        if (featureMembers == null || featureMembers.isEmpty()) {
            throw new RuntimeException("Адрес не найден");
        }

        return featureMembers.stream()
                .map(fm -> (Map<String, Object>) fm.get("GeoObject"))
                .map(this::parseGeoObject)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private GeoResult parseGeoObject(Map<String, Object> geoObject) {
        Map<String, Object> point = (Map<String, Object>) geoObject.get("Point");
        String[] coords = ((String) point.get("pos")).split(" ");
        double lon = Double.parseDouble(coords[0]);
        double lat = Double.parseDouble(coords[1]);

        Map<String, Object> metaData = (Map<String, Object>) geoObject.get("metaDataProperty");
        Map<String, Object> geocoderMeta = (Map<String, Object>) metaData.get("GeocoderMetaData");
        Map<String, Object> address = (Map<String, Object>) geocoderMeta.get("Address");

        String country = getValue(address, "country");
        String city = getValue(address, "locality");
        String region = getValue(address, "province");
        String street = getValue(address, "street");
        String house = getValue(address, "house");
        String postalCode = (String) address.get("postal_code");

        return GeoResult.builder()
                .street(street)
                .houseNumber(house)
                .city(city)
                .region(region)
                .country(country)
                .postalCode(postalCode)
                .latitude(lat)
                .longitude(lon)
                .placeId((String) geoObject.get("name"))
                .build();
    }
    @SuppressWarnings("unchecked")
    private String getValue(Map<String, Object> address, String kind) {
        if (address == null) return null;
        List<Map<String, Object>> components = (List<Map<String, Object>>) address.get("Components");
        if (components == null) return null;
        for (Map<String, Object> component : components) {
            if (kind.equalsIgnoreCase((String) component.get("kind"))) {
                return (String) component.get("name");
            }
        }
        return null;
    }
}
