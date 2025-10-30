package org.simpletaxi.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
public class GateWayConfig {

    @Bean
    public AntPathMatcher getAntPathMatcher() {
        return new AntPathMatcher();
    }
}
