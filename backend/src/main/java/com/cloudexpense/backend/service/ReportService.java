package com.cloudexpense.backend.service;

import com.cloudexpense.backend.dto.ExpenseReportResponse;
import com.cloudexpense.backend.entity.Expense;
import com.cloudexpense.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final ExpenseRepository expenseRepository;

    public ReportService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ExpenseReportResponse generateExpenseReport() {

        List<Expense> expenses =
                expenseRepository.findAll();

        long totalExpenses = expenses.size();

        BigDecimal totalAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingExpenses = expenses.stream()
                .filter(expense ->
                        expense.getStatus() == Expense.Status.PENDING)
                .count();

        long approvedExpenses = expenses.stream()
                .filter(expense ->
                        expense.getStatus() == Expense.Status.APPROVED)
                .count();

        long rejectedExpenses = expenses.stream()
                .filter(expense ->
                        expense.getStatus() == Expense.Status.REJECTED)
                .count();

        Map<String, BigDecimal> amountByCategory =
                new LinkedHashMap<>();

        for (Expense expense : expenses) {

            amountByCategory.merge(
                    expense.getCategory(),
                    expense.getAmount(),
                    BigDecimal::add
            );
        }

        return new ExpenseReportResponse(
                totalExpenses,
                totalAmount,
                pendingExpenses,
                approvedExpenses,
                rejectedExpenses,
                amountByCategory
        );
    }
}