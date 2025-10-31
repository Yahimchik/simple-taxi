package org.simpletaxi.internalauthstarter.config;

import org.simpletaxi.internalauthstarter.service.InternalJwtService;
import org.simpletaxi.internalauthstarter.service.impl.InternalJwtServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.AntPathMatcher;

@AutoConfiguration
@ConditionalOnProperty(prefix = "internal.jwt", name = "enabled", havingValue = "true", matchIfMissing = true)
public class InternalAuthAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public InternalJwtService internalJwtService(@Value("${internal.jwt.secret}") String secret) {
        return new InternalJwtServiceImpl(secret);
    }

    @Bean
    public AntPathMatcher getAntPathMatcher() {
        return new AntPathMatcher();
    }
}
