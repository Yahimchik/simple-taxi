package org.simpletaxi.gateway.config;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerAggregationConfig {

    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        SwaggerUiConfigParameters configParameters = new SwaggerUiConfigParameters(swaggerUiConfigProperties);
        configParameters.addGroup("auth-service");
        configParameters.addGroup("user-service");

        configParameters.addUrl("/v3/api-docs/auth-service");
        configParameters.addUrl("/v3/api-docs/user-service");

        return configParameters;
    }
}