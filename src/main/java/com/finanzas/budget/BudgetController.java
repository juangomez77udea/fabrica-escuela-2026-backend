package com.finanzas.budget;

import com.finanzas.budget.dto.BudgetRequest;
import com.finanzas.budget.dto.BudgetResponse;
import com.finanzas.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<Budget> createOrUpdateBudget(@AuthenticationPrincipal User user, @RequestBody BudgetRequest request) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Budget budget = budgetService.createOrUpdateBudget(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
        @AuthenticationPrincipal User user,
        @RequestParam Integer month,
        @RequestParam Integer year) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<BudgetResponse> budgets = budgetService.getBudgetsForUser(user, month, year);
        return ResponseEntity.ok(budgets);
    }
}