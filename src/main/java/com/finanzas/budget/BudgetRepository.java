package com.finanzas.budget;

import com.finanzas.category.Category;
import com.finanzas.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserAndCategoryAndMonthAndYear(User user, Category category, Integer month, Integer year);

    List<Budget> findByUser(User user);

    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.month = :month AND b.year = :year")
    List<Budget> findByUserAndMonthAndYear(@Param("user") User user, @Param("month") Integer month, @Param("year") Integer year);
}