package com.simple.taxi.auth.config;

import com.simple.taxi.auth.model.enums.LoginType;
import com.simple.taxi.auth.service.social.SocialAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class SocialConfig {
    private final List<SocialAuthProvider> socialAuthProviderMap;

    @Bean
    public Map<LoginType, SocialAuthProvider> socialAuthProviderMap() {
        return socialAuthProviderMap.stream()
                .collect(Collectors.toMap(SocialAuthProvider::getType, Function.identity()));
    }
}