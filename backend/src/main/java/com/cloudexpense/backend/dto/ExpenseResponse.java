package com.cloudexpense.backend.dto;

import com.cloudexpense.backend.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
    Long id,
    Long employeeId,
    String employeeName,
    BigDecimal amount,
    String category,
    LocalDate expenseDate,
    String description,
    Expense.Status status,
    String rejectionReason,
    String receiptFileName,
    String receiptBlobName,
    String receiptUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}