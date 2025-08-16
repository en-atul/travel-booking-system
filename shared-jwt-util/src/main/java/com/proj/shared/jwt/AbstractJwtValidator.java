package com.proj.shared.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Collection;
import java.util.Date;

public abstract class AbstractJwtValidator implements JwtValidator {

    protected final String jwtSecret;

    protected AbstractJwtValidator(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    protected Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    @Override
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String extractTokenId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getId();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Long extractUserId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Long.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<String> extractRoles(String token) {
        try {
            return (Collection<String>) Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("roles", Collection.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    protected Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    protected boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims != null && claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    protected String extractTokenType(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims != null ? claims.get("type", String.class) : null;
        } catch (Exception e) {
            return null;
        }
    }
} 