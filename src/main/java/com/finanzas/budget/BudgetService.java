package com.finanzas.budget;

import com.finanzas.budget.dto.BudgetRequest;
import com.finanzas.budget.dto.BudgetResponse;
import com.finanzas.category.Category;
import com.finanzas.category.CategoryRepository;
import com.finanzas.transaction.TransactionRepository;
import com.finanzas.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public Budget createOrUpdateBudget(User user, BudgetRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));

        Optional<Budget> existing = budgetRepository.findByUserAndCategoryAndMonthAndYear(
            user, category, request.month(), request.year());

        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setAmount(request.amount());
        } else {
            budget = new Budget();
            budget.setUser(user);
            budget.setCategory(category);
            budget.setMonth(request.month());
            budget.setYear(request.year());
            budget.setAmount(request.amount());
        }

        return budgetRepository.save(budget);
    }

    public List<BudgetResponse> getBudgetsForUser(User user, Integer month, Integer year) {
        List<Budget> budgets = budgetRepository.findByUserAndMonthAndYear(user, month, year);

        return budgets.stream().map(budget -> {
            BigDecimal spent = calculateSpent(user, budget.getCategory(), month, year);
            BigDecimal remaining = budget.getAmount().subtract(spent);
            return new BudgetResponse(
                budget.getId(),
                budget.getCategory(),
                budget.getAmount(),
                budget.getMonth(),
                budget.getYear(),
                spent,
                remaining
            );
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateSpent(User user, Category category, Integer month, Integer year) {
        return transactionRepository.sumAmountByUserAndCategoryAndMonthAndYearAndType(
            user, category, month, year, "EXPENSE");
    }

    public boolean checkBudgetExceeded(User user, Category category, BigDecimal amount, Integer month, Integer year) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserAndCategoryAndMonthAndYear(user, category, month, year);
        if (budgetOpt.isEmpty()) {
            return false; // No budget set, allow
        }
        BigDecimal currentSpent = calculateSpent(user, category, month, year);
        return currentSpent.add(amount).compareTo(budgetOpt.get().getAmount()) > 0;
    }
}