package com.cloudexpense.backend.controller;

import com.cloudexpense.backend.dto.ExpenseRequest;
import com.cloudexpense.backend.dto.ExpenseResponse;
import com.cloudexpense.backend.dto.ReceiptDownloadResponse;
import com.cloudexpense.backend.service.ExpenseService;
import com.cloudexpense.backend.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ReceiptService receiptService;

    public ExpenseController(
            ExpenseService expenseService,
            ReceiptService receiptService) {

        this.expenseService = expenseService;
        this.receiptService = receiptService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        ExpenseResponse response =
                expenseService.createExpense(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getMyExpenses(
            Authentication authentication) {

        return ResponseEntity.ok(
                expenseService.getMyExpenses(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getMyExpense(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                expenseService.getMyExpense(
                        id,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{id}/receipt")
    public ResponseEntity<ExpenseResponse> uploadReceipt(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        ExpenseResponse response =
                receiptService.uploadReceipt(
                        id,
                        authentication.getName(),
                        file
                );

        return ResponseEntity.ok(response);
    }
  @GetMapping("/{id}/receipt")
public ResponseEntity<Resource> downloadReceipt(
        @PathVariable Long id,
        Authentication authentication) {

    ReceiptDownloadResponse response =
            receiptService.downloadReceipt(
                    id,
                    authentication.getName()
            );

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" +
                            response.fileName() + "\""
            )
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(response.resource());
}
}