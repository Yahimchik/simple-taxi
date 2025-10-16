package com.simple.taxi.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.taxi.user.utils.YandexJwtGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.time.Instant;
import java.util.Map;

@Component
public class YandexIamTokenProvider {

    @Value("${yandex.geocoder.key-file}")
    private String keyFilePath;

    private String cachedToken;
    private Instant expiresAt;

    private final WebClient webClient = WebClient.create("https://iam.api.cloud.yandex.net/iam/v1/tokens");

    public Mono<String> getToken() {
        if (cachedToken != null && Instant.now().isBefore(expiresAt.minusSeconds(30))) {
            return Mono.just(cachedToken);
        }

        return Mono.fromCallable(() -> {
            // Читаем JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> keyJson = mapper.readValue(new File(keyFilePath), Map.class);

            String jwt = YandexJwtGenerator.generateJwt(keyJson); // реализовать самостоятельно

            Map<String, String> body = Map.of("jwt", jwt);
            Map response = webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            cachedToken = (String) response.get("iamToken");
            expiresAt = Instant.now().plusSeconds(3600);

            return cachedToken;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
