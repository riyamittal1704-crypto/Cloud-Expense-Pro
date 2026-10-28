package com.cloudexpense.backend.dto;

import org.springframework.core.io.Resource;

public record ReceiptDownloadResponse(
        Resource resource,
        String fileName
) {
}