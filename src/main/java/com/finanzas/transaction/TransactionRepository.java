package com.finanzas.transaction;

import com.finanzas.category.Category;
import com.finanzas.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate inicio, LocalDate fin);
    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.category = :category AND EXTRACT(MONTH FROM t.transactionDate) = :month AND EXTRACT(YEAR FROM t.transactionDate) = :year AND t.type = :type")
    BigDecimal sumAmountByUserAndCategoryAndMonthAndYearAndType(@Param("user") User user, @Param("category") Category category, @Param("month") Integer month, @Param("year") Integer year, @Param("type") String type);
}
