package com.finanzas.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
    @NotBlank(message = "El nombre es requerido")
    String name,

    String descripcion,

    @NotBlank(message = "El tipo es requerido (INGRESO o GASTO)")
    @NotNull(message = "El tipo no puede ser nulo")
    String type
) {
}
