package com.proj.shared.jwt;

import java.time.LocalDateTime;

public class AuthErrorResponse {
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private int status;

    public AuthErrorResponse() {}

    public AuthErrorResponse(String error, String message, String path, int status) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    // Static factory methods for common errors
    public static AuthErrorResponse unauthorized(String path) {
        return new AuthErrorResponse("UNAUTHORIZED", "Access token is missing or invalid", path, 401);
    }

    public static AuthErrorResponse forbidden(String path, String reason) {
        return new AuthErrorResponse("FORBIDDEN", reason, path, 403);
    }

    public static AuthErrorResponse tokenExpired(String path) {
        return new AuthErrorResponse("TOKEN_EXPIRED", "Access token has expired", path, 401);
    }

    public static AuthErrorResponse invalidToken(String path) {
        return new AuthErrorResponse("INVALID_TOKEN", "Access token format is invalid", path, 401);
    }

    public static AuthErrorResponse insufficientPrivileges(String path) {
        return new AuthErrorResponse("INSUFFICIENT_PRIVILEGES", "User does not have required roles", path, 403);
    }
} 