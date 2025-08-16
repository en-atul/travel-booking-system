package com.proj.apigateway.security;

import com.proj.shared.jwt.AbstractJwtValidator;
import com.proj.shared.jwt.JwtClaims;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GatewayJwtValidator extends AbstractJwtValidator {

    public GatewayJwtValidator(@Value("${auth.token.jwtSecret}") String jwtSecret) {
        super(jwtSecret);
    }

    @Override
    public JwtClaims validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return new JwtClaims(null, null, null, null, null, false);
        }

        try {
            Claims claims = parseClaims(token);
            if (claims == null) {
                return new JwtClaims(null, null, null, null, null, false);
            }

            // Check if token is expired
            if (isTokenExpired(token)) {
                return new JwtClaims(
                        claims.getId(),
                        claims.getSubject(),
                        claims.get("userId", Long.class),
                        (java.util.Collection<String>) claims.get("roles"),
                        claims.get("type", String.class),
                        false
                );
            }

            // For API Gateway, we only validate signature and expiration
            // Database validation should be done by auth-service for its own endpoints
            String tokenType = extractTokenType(token);
            if (!"ACCESS".equals(tokenType)) {
                return new JwtClaims(
                        claims.getId(),
                        claims.getSubject(),
                        claims.get("userId", Long.class),
                        (java.util.Collection<String>) claims.get("roles"),
                        tokenType,
                        false
                );
            }

            return new JwtClaims(
                    claims.getId(),
                    claims.getSubject(),
                    claims.get("userId", Long.class),
                    (java.util.Collection<String>) claims.get("roles"),
                    tokenType,
                    true
            );

        } catch (Exception e) {
            return new JwtClaims(null, null, null, null, null, false);
        }
    }
}
