package com.cloudexpense.backend.controller;

import com.cloudexpense.backend.dto.ExpenseResponse;
import com.cloudexpense.backend.dto.ReceiptDownloadResponse;
import com.cloudexpense.backend.dto.RejectExpenseRequest;
import com.cloudexpense.backend.service.AdminExpenseService;
import com.cloudexpense.backend.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/expenses")
public class AdminExpenseController {

    private final AdminExpenseService adminExpenseService;
    private final ReceiptService receiptService;

    public AdminExpenseController(
            AdminExpenseService adminExpenseService,
            ReceiptService receiptService) {

        this.adminExpenseService = adminExpenseService;
        this.receiptService = receiptService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {

        return ResponseEntity.ok(
                adminExpenseService.getAllExpenses()
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ExpenseResponse>> getPendingExpenses() {

        return ResponseEntity.ok(
                adminExpenseService.getPendingExpenses()
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ExpenseResponse> approveExpense(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminExpenseService.approveExpense(id)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ExpenseResponse> rejectExpense(
            @PathVariable Long id,
            @Valid @RequestBody RejectExpenseRequest request) {

        return ResponseEntity.ok(
                adminExpenseService.rejectExpense(
                        id,
                        request.getReason()
                )
        );
    }

    // =========================================================
    // ADMIN RECEIPT DOWNLOAD
    // =========================================================

    @GetMapping("/{id}/receipt")
    public ResponseEntity<Resource> downloadReceipt(
            @PathVariable Long id) {

        ReceiptDownloadResponse response =
                receiptService.downloadReceiptForAdmin(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                response.fileName() +
                                "\""
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(response.resource());
    }
}