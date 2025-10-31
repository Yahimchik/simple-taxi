package org.simpletaxi.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerAggregationConfig {

    // 🧩 Добавляем поддержку Bearer токена в Swagger UI
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }


    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        SwaggerUiConfigParameters configParameters = new SwaggerUiConfigParameters(swaggerUiConfigProperties);
        configParameters.addGroup("auth-service");
        configParameters.addGroup("user-service");

        configParameters.addUrl("/auth-service/v3/api-docs");
        configParameters.addUrl("/user-service/v3/api-docs");

        return configParameters;
    }
}