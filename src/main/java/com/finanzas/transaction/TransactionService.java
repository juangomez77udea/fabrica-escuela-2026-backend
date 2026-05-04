package com.finanzas.transaction;

import com.finanzas.budget.BudgetService;
import com.finanzas.category.Category;
import com.finanzas.category.CategoryRepository;
import com.finanzas.transaction.dto.CategoryDTO;
import com.finanzas.transaction.dto.CreateTransactionRequest;
import com.finanzas.transaction.dto.TransactionResponse;
import com.finanzas.transaction.dto.UpdateTransactionRequest;
import com.finanzas.user.User;
import com.finanzas.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;

    public TransactionService(
        TransactionRepository transactionRepository,
        UserRepository userRepository,
        CategoryRepository categoryRepository,
        BudgetService budgetService
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.budgetService = budgetService;
    }

    public TransactionResponse createTransaction(Long userId, CreateTransactionRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if ("EXPENSE".equals(request.type())) {
            int month = request.transactionDate().getMonthValue();
            int year = request.transactionDate().getYear();
            boolean exceeded = budgetService.checkBudgetExceeded(user, category, request.amount(), month, year);
            if (exceeded) {
                throw new IllegalArgumentException("El gasto supera el presupuesto establecido para esta categoría");
            }
        }

        Transaction transaction = new Transaction(
            user,
            category,
            request.amount(),
            request.transactionDate(),
            request.description(),
            request.type()
        );

        Transaction saved = transactionRepository.save(transaction);
        return toTransactionResponse(saved);
    }

    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
            .map(this::toTransactionResponse)
            .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
        return toTransactionResponse(transaction);
    }

    public TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));

        Category newCategory = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        // Validar presupuesto si es EXPENSE y hubo cambios
        if ("EXPENSE".equals(request.type())) {
            int month = request.transactionDate().getMonthValue();
            int year = request.transactionDate().getYear();
            
            // Si cambió la categoría o el monto, verificar presupuesto
            if (!transaction.getCategory().getId().equals(request.categoryId()) || 
                !transaction.getAmount().equals(request.amount())) {
                
                // Restar el monto anterior y sumar el nuevo para comparar
                java.math.BigDecimal netAmount = request.amount().subtract(transaction.getAmount());
                boolean exceeded = budgetService.checkBudgetExceeded(transaction.getUser(), newCategory, netAmount, month, year);
                if (exceeded) {
                    throw new IllegalArgumentException("El gasto supera el presupuesto establecido para esta categoría");
                }
            }
        }

        // Actualizar campos
        transaction.setCategory(newCategory);
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setDescription(request.description());
        transaction.setType(request.type());

        Transaction updated = transactionRepository.save(transaction);
        return toTransactionResponse(updated);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
        transactionRepository.delete(transaction);
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        CategoryDTO categoryDTO = new CategoryDTO(
            transaction.getCategory().getId(),
            transaction.getCategory().getName(),
            transaction.getCategory().getDescription(),
            transaction.getCategory().getType(),
            transaction.getCategory().getIsActive()
        );

        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getTransactionDate(),
            transaction.getDescription(),
            transaction.getType(),
            categoryDTO,
            transaction.getCreatedAt()
        );
    }
}
