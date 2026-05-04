package com.finanzas.auth.dto;

public record AuthResponse(
        String message,
        String token,
        UserResponse user
) {
}
