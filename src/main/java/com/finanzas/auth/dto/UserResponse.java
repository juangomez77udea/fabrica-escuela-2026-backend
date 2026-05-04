package com.finanzas.auth.dto;

import com.finanzas.user.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String nombre,
        String email,
        LocalDateTime createdAt,
        UserRole role
) {
}
