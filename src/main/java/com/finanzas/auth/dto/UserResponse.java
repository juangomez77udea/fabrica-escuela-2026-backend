package com.finanzas.auth.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String nombre,
        String email,
        LocalDateTime createdAt
) {
}
