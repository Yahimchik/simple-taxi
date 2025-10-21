package org.example.geo.service;

import org.example.geo.model.dto.GeoResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GeocodingService {
    Mono<List<GeoResult>> geocode(String formattedAddress, Double latitude, Double longitude);
}