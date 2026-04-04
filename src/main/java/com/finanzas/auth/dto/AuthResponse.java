package com.finanzas.auth.dto;

public record AuthResponse(
        String message,
        UserResponse user
) {
}
