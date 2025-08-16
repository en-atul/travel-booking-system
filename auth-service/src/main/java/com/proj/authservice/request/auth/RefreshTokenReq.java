package com.proj.authservice.request.auth;

import jakarta.validation.constraints.NotEmpty;

public record RefreshTokenReq(@NotEmpty String refreshToken) {
}