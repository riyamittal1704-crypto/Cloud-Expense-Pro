package com.cloudexpense.backend.service;

import com.cloudexpense.backend.dto.ExpenseResponse;
import com.cloudexpense.backend.entity.Expense;
import com.cloudexpense.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminExpenseService {

    private final ExpenseRepository expenseRepository;

    public AdminExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseResponse> getAllExpenses() {

        return expenseRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ExpenseResponse> getPendingExpenses() {

        return expenseRepository
                .findAllByStatusOrderByCreatedAtDesc(
                        Expense.Status.PENDING
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ExpenseResponse approveExpense(Long expenseId) {

        Expense expense = findExpense(expenseId);

        if (expense.getStatus() != Expense.Status.PENDING) {
            throw new IllegalStateException(
                    "Only pending expenses can be approved"
            );
        }

        expense.setStatus(Expense.Status.APPROVED);
        expense.setRejectionReason(null);

        Expense savedExpense = expenseRepository.save(expense);

        return toResponse(savedExpense);
    }

    public ExpenseResponse rejectExpense(
            Long expenseId,
            String reason) {

        Expense expense = findExpense(expenseId);

        if (expense.getStatus() != Expense.Status.PENDING) {
            throw new IllegalStateException(
                    "Only pending expenses can be rejected"
            );
        }

        expense.setStatus(Expense.Status.REJECTED);
        expense.setRejectionReason(reason);

        Expense savedExpense = expenseRepository.save(expense);

        return toResponse(savedExpense);
    }

    private Expense findExpense(Long expenseId) {

        return expenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Expense not found"
                        )
                );
    }

    private ExpenseResponse toResponse(Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getEmployee().getId(),
                expense.getEmployee().getName(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getExpenseDate(),
                expense.getDescription(),
                expense.getStatus(),
                expense.getRejectionReason(),
                expense.getReceiptFileName(),
                expense.getReceiptBlobName(),
                expense.getReceiptUrl(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}