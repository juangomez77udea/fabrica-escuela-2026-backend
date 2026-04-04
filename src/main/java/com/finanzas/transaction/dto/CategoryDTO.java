package com.finanzas.transaction.dto;

public record CategoryDTO(
    Long id,
    String name,
    String descripcion,
    String type,
    Boolean isActive
) {
}
