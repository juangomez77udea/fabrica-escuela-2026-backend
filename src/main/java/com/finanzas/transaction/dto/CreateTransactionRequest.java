package com.finanzas.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransactionRequest(
    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser positivo")
    BigDecimal amount,

    @NotNull(message = "La fecha es requerida")
    LocalDate transactionDate,

    @NotNull(message = "El ID de categoría es requerido")
    Long categoryId,

    @NotBlank(message = "La descripción es requerida")
    String description,

    @NotBlank(message = "El tipo es requerido (INGRESO o GASTO)")
    String type
) {
}
