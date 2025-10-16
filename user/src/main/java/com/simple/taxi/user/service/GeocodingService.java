package com.simple.taxi.user.service;

import com.simple.taxi.user.model.dto.GeoResult;
import reactor.core.publisher.Mono;

public interface GeocodingService {
    Mono<GeoResult> geocode(String formattedAddress, Double latitude, Double longitude);
}