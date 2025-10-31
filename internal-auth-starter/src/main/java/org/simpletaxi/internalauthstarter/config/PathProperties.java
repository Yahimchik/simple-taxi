package org.simpletaxi.internalauthstarter.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class PathProperties {

    private List<String> publicPaths = new ArrayList<>();

    private static final List<String> DEFAULT_PATHS = Arrays.asList(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**"
    );

    @PostConstruct
    public void init() {
        if (publicPaths.isEmpty()) {
            publicPaths.addAll(DEFAULT_PATHS);
        } else {
            DEFAULT_PATHS.stream()
                    .filter(defaultPath -> !publicPaths.contains(defaultPath))
                    .forEach(publicPaths::add);
        }
        log.info("✅ Loaded public paths: {}", publicPaths);
    }
}