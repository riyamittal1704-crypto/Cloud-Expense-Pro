package com.cloudexpense.backend.service;

import com.cloudexpense.backend.dto.ExpenseRequest;
import com.cloudexpense.backend.dto.ExpenseResponse;
import com.cloudexpense.backend.entity.Expense;
import com.cloudexpense.backend.entity.user;
import com.cloudexpense.backend.repository.ExpenseRepository;
import com.cloudexpense.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository) {

        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public ExpenseResponse createExpense(
            ExpenseRequest request,
            String email) {

        user employee = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Expense expense = new Expense();

        expense.setEmployee(employee);
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setDescription(request.getDescription());
        expense.setStatus(Expense.Status.PENDING);

        Expense savedExpense = expenseRepository.save(expense);

        return toResponse(savedExpense);
    }

    public List<ExpenseResponse> getMyExpenses(String email) {

        return expenseRepository
                .findAllByEmployee_EmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ExpenseResponse getMyExpense(
            Long expenseId,
            String email) {

        Expense expense = expenseRepository
                .findByIdAndEmployee_Email(expenseId, email)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found")
                );

        return toResponse(expense);
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