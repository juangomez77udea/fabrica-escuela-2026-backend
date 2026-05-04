package com.finanzas.budget.dto;

import com.finanzas.category.Category;

import java.math.BigDecimal;

public record BudgetResponse(
    Long id,
    Category category,
    BigDecimal amount,
    Integer month,
    Integer year,
    BigDecimal spent,
    BigDecimal remaining
) {}