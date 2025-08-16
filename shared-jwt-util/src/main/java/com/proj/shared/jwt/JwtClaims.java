package com.proj.shared.jwt;

import java.util.Collection;

public class JwtClaims {
    private String tokenId;
    private String username;
    private Long userId;
    private Collection<String> roles;
    private String tokenType;
    private boolean isValid;

    public JwtClaims() {}

    public JwtClaims(String tokenId, String username, Long userId, Collection<String> roles, String tokenType, boolean isValid) {
        this.tokenId = tokenId;
        this.username = username;
        this.userId = userId;
        this.roles = roles;
        this.tokenType = tokenType;
        this.isValid = isValid;
    }

    // Getters and Setters
    public String getTokenId() { return tokenId; }
    public void setTokenId(String tokenId) { this.tokenId = tokenId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Collection<String> getRoles() { return roles; }
    public void setRoles(Collection<String> roles) { this.roles = roles; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasAnyRole(Collection<String> requiredRoles) {
        return roles != null && roles.stream().anyMatch(requiredRoles::contains);
    }
} 