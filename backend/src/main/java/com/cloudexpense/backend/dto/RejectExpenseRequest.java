package com.cloudexpense.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectExpenseRequest {

    @NotBlank(message = "Rejection reason is required")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}