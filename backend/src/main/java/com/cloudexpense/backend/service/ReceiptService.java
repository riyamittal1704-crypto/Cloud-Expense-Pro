package com.cloudexpense.backend.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.cloudexpense.backend.dto.ExpenseResponse;
import com.cloudexpense.backend.dto.ReceiptDownloadResponse;
import com.cloudexpense.backend.entity.Expense;
import com.cloudexpense.backend.repository.ExpenseRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

@Service
public class ReceiptService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final ExpenseRepository expenseRepository;
    private final BlobContainerClient blobContainerClient;

    public ReceiptService(
            ExpenseRepository expenseRepository,
            BlobContainerClient blobContainerClient) {

        this.expenseRepository = expenseRepository;
        this.blobContainerClient = blobContainerClient;
    }

    // =========================================================
    // UPLOAD RECEIPT
    // =========================================================

    public ExpenseResponse uploadReceipt(
            Long expenseId,
            String email,
            MultipartFile file) {

        // 1. Validate file exists
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Receipt file is required");
        }

        // 2. Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Receipt file must not exceed 10 MB");
        }

        // 3. Get file information
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Receipt file name is required");
        }

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new IllegalArgumentException(
                    "Receipt file type could not be determined");
        }

        // 4. Validate allowed file types
        String extension = getExtension(originalFileName);

        if (!isAllowedFile(contentType, extension)) {
            throw new IllegalArgumentException(
                    "Only PDF, JPG, JPEG and PNG files are allowed");
        }

        // 5. Find employee's expense
        Expense expense = expenseRepository
                .findByIdAndEmployee_Email(expenseId, email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Expense not found"));

        // 6. Only pending expenses can receive/change receipts
        if (expense.getStatus() != Expense.Status.PENDING) {
            throw new IllegalStateException(
                    "Receipt can only be uploaded for a pending expense");
        }

        // 7. Generate unique blob name
        String blobName =
                "expense-" + expenseId + "-" + UUID.randomUUID()
                        + "." + extension;

        BlobClient blobClient =
                blobContainerClient.getBlobClient(blobName);

        try {

            // 8. Upload file to Azure Blob Storage
            try (InputStream inputStream = file.getInputStream()) {

                blobClient.upload(
                        inputStream,
                        file.getSize(),
                        true
                );
            }

            // 9. Set correct content type
            blobClient.setHttpHeaders(
                    new BlobHttpHeaders()
                            .setContentType(contentType)
            );

            // 10. Save receipt information in database
            expense.setReceiptFileName(originalFileName);
            expense.setReceiptBlobName(blobName);
            expense.setReceiptUrl(blobClient.getBlobUrl());

            Expense savedExpense =
                    expenseRepository.save(expense);

            // 11. Return updated expense
            return toResponse(savedExpense);

        } catch (IOException e) {

            // Remove blob if upload/database process fails
            try {
                if (blobClient.exists()) {
                    blobClient.delete();
                }
            } catch (Exception ignored) {
            }

            throw new RuntimeException(
                    "Failed to upload receipt", e);
        }
    }

    // =========================================================
    // DOWNLOAD RECEIPT FOR EMPLOYEE
    // =========================================================

    public ReceiptDownloadResponse downloadReceipt(
            Long expenseId,
            String email) {

        // 1. Find expense belonging to authenticated employee
        Expense expense = expenseRepository
                .findByIdAndEmployee_Email(expenseId, email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Expense not found"));

        // 2. Check whether receipt exists in database
        if (expense.getReceiptBlobName() == null ||
                expense.getReceiptBlobName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Receipt not found");
        }

        // 3. Get blob from Azure Blob Storage
        BlobClient blobClient =
                blobContainerClient.getBlobClient(
                        expense.getReceiptBlobName());

        // 4. Check whether blob actually exists
        if (!blobClient.exists()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Receipt file not found in storage");
        }

        // 5. Open blob as an input stream
        Resource resource =
                new InputStreamResource(
                        blobClient.openInputStream());

        // 6. Return resource + original filename
        return new ReceiptDownloadResponse(
                resource,
                expense.getReceiptFileName()
        );
    }

    // =========================================================
    // DOWNLOAD RECEIPT FOR ADMIN
    // =========================================================

    public ReceiptDownloadResponse downloadReceiptForAdmin(
            Long expenseId) {

        // 1. Find expense by ID
        Expense expense = expenseRepository
                .findById(expenseId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Expense not found"));

        // 2. Check whether receipt exists in database
        if (expense.getReceiptBlobName() == null ||
                expense.getReceiptBlobName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Receipt not found");
        }

        // 3. Get blob from Azure Blob Storage
        BlobClient blobClient =
                blobContainerClient.getBlobClient(
                        expense.getReceiptBlobName());

        // 4. Verify that blob actually exists
        if (!blobClient.exists()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Receipt file not found in storage");
        }

        // 5. Open blob as an input stream
        Resource resource =
                new InputStreamResource(
                        blobClient.openInputStream());

        // 6. Return resource + original filename
        return new ReceiptDownloadResponse(
                resource,
                expense.getReceiptFileName()
        );
    }

    // =========================================================
    // GET FILE EXTENSION
    // =========================================================

    private String getExtension(String fileName) {

        int lastDot = fileName.lastIndexOf('.');

        if (lastDot == -1 ||
                lastDot == fileName.length() - 1) {

            throw new IllegalArgumentException(
                    "Receipt file must have a valid extension");
        }

        return fileName
                .substring(lastDot + 1)
                .toLowerCase(Locale.ROOT);
    }

    // =========================================================
    // VALIDATE FILE TYPE
    // =========================================================

    private boolean isAllowedFile(
            String contentType,
            String extension) {

        return
                (
                        "application/pdf".equalsIgnoreCase(contentType)
                                && "pdf".equals(extension)
                )

                ||

                (
                        "image/jpeg".equalsIgnoreCase(contentType)
                                && (
                                "jpg".equals(extension)
                                        || "jpeg".equals(extension)
                        )
                )

                ||

                (
                        "image/png".equalsIgnoreCase(contentType)
                                && "png".equals(extension)
                );
    }

    // =========================================================
    // CONVERT EXPENSE TO RESPONSE
    // =========================================================

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