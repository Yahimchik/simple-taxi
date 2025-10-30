package org.simpletaxi.internalauthstarter.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpletaxi.internalauthstarter.config.PublicEndpointRegistry;
import org.simpletaxi.internalauthstarter.service.InternalJwtService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalAuthFilter implements WebFilter {

    private final InternalJwtService internalJwtService;
    private final PublicEndpointRegistry publicEndpointRegistry;

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (publicEndpointRegistry.isPublic(path)) {
            log.debug("Public endpoint, skipping internal auth: {}", path);
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst("X-Internal-Token");
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Map<String, Object> claims = internalJwtService.validateInternalToken(token);
            exchange.getAttributes().put("userId", claims.get("userId"));
            exchange.getAttributes().put("role", claims.get("role"));
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("Invalid internal token: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
    }
}
