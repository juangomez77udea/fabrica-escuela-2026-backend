package com.finanzas.budget.dto;

import java.math.BigDecimal;

public record BudgetRequest(
    Long categoryId,
    BigDecimal amount,
    Integer month,
    Integer year
) {}