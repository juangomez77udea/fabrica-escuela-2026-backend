package com.finanzas.transaction;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.finanzas.category.Category;
import com.finanzas.category.CategoryRepository;
import com.finanzas.transaction.dto.CategoryDTO;
import com.finanzas.transaction.dto.CreateTransactionRequest;
import com.finanzas.transaction.dto.TransactionResponse;
import com.finanzas.user.User;
import com.finanzas.user.UserRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(
        TransactionRepository transactionRepository,
        UserRepository userRepository,
        CategoryRepository categoryRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public TransactionResponse createTransaction(Long userId, CreateTransactionRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

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

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
        transactionRepository.delete(transaction);
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        CategoryDTO categoryDTO = new CategoryDTO(
            transaction.getCategory().getId(),
            transaction.getCategory().getName(),
            transaction.getCategory().getDescripcion(),
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
