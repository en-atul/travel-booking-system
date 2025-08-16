package com.proj.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.shared.jwt.AuthErrorResponse;
import com.proj.shared.jwt.JwtClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final GatewayJwtValidator jwtValidator;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher;

    // public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/v1/auth/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    // role-based access control for different endpoints
    private static final Map<String, List<String>> ROLE_REQUIREMENTS = Map.of(
            "/account/**", Arrays.asList("USER", "ADMIN"),
            "/customer/**", Arrays.asList("USER", "ADMIN"),
            "/transaction/**", Arrays.asList("USER", "ADMIN"),
            "/ledger/**", Arrays.asList("ADMIN"),
            "/notification/**", Arrays.asList("USER", "ADMIN")
    );

    public AuthenticationFilter(GatewayJwtValidator jwtValidator, ObjectMapper objectMapper) {
        this.jwtValidator = jwtValidator;
        this.objectMapper = objectMapper;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        logger.info("Processing request: {} {}", method, path);

        // Check if it's a public endpoint
        if (isPublicEndpoint(path)) {
            logger.info("Public endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        // Extract token from Authorization header
        String token = extractTokenFromHeader(exchange);
        if (token == null) {
            logger.warn("No authorization token found for path: {}", path);
            return handleAuthError(exchange, AuthErrorResponse.unauthorized(path));
        }

        // Validate token
        JwtClaims claims = jwtValidator.validateToken(token);
        if (!claims.isValid()) {
            logger.warn("Invalid token for path: {}", path);
            return handleAuthError(exchange, AuthErrorResponse.unauthorized(path));
        }

        // Check role-based access
        if (!hasRequiredRole(path, claims)) {
            logger.warn("Insufficient privileges for user {} on path: {}", claims.getUsername(), path);
            return handleAuthError(exchange, AuthErrorResponse.insufficientPrivileges(path));
        }

        // Add user information to headers for downstream services
        addUserInfoToHeaders(exchange, claims);

        logger.info("Authentication successful for user: {} on path: {}", claims.getUsername(), path);
        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String extractTokenFromHeader(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean hasRequiredRole(String path, JwtClaims claims) {
        for (Map.Entry<String, List<String>> entry : ROLE_REQUIREMENTS.entrySet()) {
            if (pathMatcher.match(entry.getKey(), path)) {
                List<String> requiredRoles = entry.getValue();
                return claims.hasAnyRole(requiredRoles);
            }
        }
        // If no specific role requirements found, allow access
        return true;
    }

    private void addUserInfoToHeaders(ServerWebExchange exchange, JwtClaims claims) {
        exchange.getRequest().mutate()
                .header("X-User-ID", String.valueOf(claims.getUserId()))
                .header("X-Username", claims.getUsername())
                .header("X-User-Roles", String.join(",", claims.getRoles()))
                .build();
    }

    private Mono<Void> handleAuthError(ServerWebExchange exchange, AuthErrorResponse errorResponse) {
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(errorResponse.getStatus()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            String errorJson = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap(errorJson.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing auth error response", e);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100; // High priority to run before other filters
    }
}
