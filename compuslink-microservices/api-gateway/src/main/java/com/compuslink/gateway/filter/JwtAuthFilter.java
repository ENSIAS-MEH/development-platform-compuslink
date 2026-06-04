package com.compuslink.gateway.filter;

import com.compuslink.common.security.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Public endpoints that don't require authentication
    private static final List<String> OPEN_PATHS = List.of(
            "/api/auth/login", "/api/auth/register", "/api/auth/refresh",
            "/api/auth/oauth2", "/oauth2", "/uploads"
    );

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        // If a valid token is present, always authenticate and forward the user id,
        // even on public paths (so the downstream can personalize when logged in).
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                String userId = jwtUtil.extractUserId(token).toString();
                ServerWebExchange mutated = exchange.mutate()
                        .request(r -> r.header("X-User-Id", userId))
                        .build();
                return chain.filter(mutated);
            }
        }

        // No valid token: allow only public paths through.
        if (isPublic(path, method)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private boolean isPublic(String path, String method) {
        if (OPEN_PATHS.stream().anyMatch(path::startsWith)) {
            return true;
        }
        // Public marketplace browsing: list and single-item detail (GET only).
        if ("GET".equals(method) && (path.equals("/api/items") || path.matches("/api/items/[^/]+"))) {
            return true;
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
