package org.example.geo.controller;

import lombok.RequiredArgsConstructor;
import org.example.geo.model.dto.GeoResult;
import org.example.geo.service.GeocodingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/geocode")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping
    public Mono<List<GeoResult>> geocode(@RequestParam String address
    ) {
        return geocodingService.geocode(address, null, null);
    }
}
