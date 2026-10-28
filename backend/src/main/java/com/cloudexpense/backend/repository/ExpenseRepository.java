package com.cloudexpense.backend.repository;

import com.cloudexpense.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByEmployee_EmailOrderByCreatedAtDesc(String email);

    Optional<Expense> findByIdAndEmployee_Email(Long id, String email);

    List<Expense> findAllByOrderByCreatedAtDesc();

    List<Expense> findAllByStatusOrderByCreatedAtDesc(
            Expense.Status status
    );
}