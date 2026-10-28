package com.cloudexpense.backend.controller;

import com.cloudexpense.backend.dto.ExpenseReportResponse;
import com.cloudexpense.backend.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/expenses")
    public ResponseEntity<ExpenseReportResponse> getExpenseReport() {

        return ResponseEntity.ok(
                reportService.generateExpenseReport()
        );
    }
}