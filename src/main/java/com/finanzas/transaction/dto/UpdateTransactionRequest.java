package com.finanzas.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(
    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser positivo")
    BigDecimal amount,

    @NotNull(message = "La fecha es requerida")
    LocalDate transactionDate,

    @NotNull(message = "El ID de categoría es requerido")
    Long categoryId,

    String description,

    String type
) {}