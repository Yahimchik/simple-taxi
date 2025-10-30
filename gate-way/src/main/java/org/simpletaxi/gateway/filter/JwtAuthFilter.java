package org.simpletaxi.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.simpletaxi.gateway.config.CustomGatewayProperties;
import org.simpletaxi.internalauthstarter.service.InternalJwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter {

    @Value("${spring.jwt.access.secret}")
    private String userSecret;

    private final AntPathMatcher pathMatcher;
    private final InternalJwtService internalJwtService;
    private final CustomGatewayProperties customGatewayProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        boolean isPublic = customGatewayProperties.getPublicPaths()
                .stream()
                .anyMatch(p -> pathMatcher.match(p, path));

        if (isPublic) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(userSecret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String internalToken = internalJwtService.generateInternalToken(
                    Map.of(
                            "userId", claims.getSubject(),
                            "role", claims.get("role", String.class)
                    )
            );

            exchange = exchange.mutate()
                    .request(r -> r.headers(h -> h.add("X-Internal-Token", internalToken)))
                    .build();

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}