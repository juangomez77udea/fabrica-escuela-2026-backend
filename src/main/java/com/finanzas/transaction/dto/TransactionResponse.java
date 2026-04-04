package com.finanzas.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    BigDecimal amount,
    LocalDate transactionDate,
    String description,
    String type,
    CategoryDTO category,
    LocalDateTime createdAt
) {
}
