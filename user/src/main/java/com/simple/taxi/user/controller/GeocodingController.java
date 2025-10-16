package com.simple.taxi.user.controller;

import com.simple.taxi.user.model.dto.GeoResult;
import com.simple.taxi.user.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/geocode")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping
    public Mono<GeoResult> geocode(@RequestParam String address
    ) {
        return geocodingService.geocode(address, null, null);
    }
}
