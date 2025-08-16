package com.proj.shared.jwt;

public interface JwtValidator {
    /**
     * Validates a JWT token and returns the claims
     * @param token The JWT token to validate
     * @return JwtClaims object containing the token information and validation status
     */
    JwtClaims validateToken(String token);
    
    /**
     * Extracts username from JWT token
     * @param token The JWT token
     * @return Username from the token
     */
    String extractUsername(String token);
    
    /**
     * Extracts user ID from JWT token
     * @param token The JWT token
     * @return User ID from the token
     */
    Long extractUserId(String token);
    
    /**
     * Extracts roles from JWT token
     * @param token The JWT token
     * @return Collection of roles from the token
     */
    java.util.Collection<String> extractRoles(String token);
    
    /**
     * Extracts token ID from JWT token
     * @param token The JWT token
     * @return Token ID from the token
     */
    String extractTokenId(String token);
} 