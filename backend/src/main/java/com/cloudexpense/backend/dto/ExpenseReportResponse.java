package com.cloudexpense.backend.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ExpenseReportResponse(
        long totalExpenses,
        BigDecimal totalAmount,
        long pendingExpenses,
        long approvedExpenses,
        long rejectedExpenses,
        Map<String, BigDecimal> amountByCategory
) {
}