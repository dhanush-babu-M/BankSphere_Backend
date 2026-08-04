package com.banksphere.core.constants;

public enum TransactionStatus {
    INITIATED("Initiated"),
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    REVERSED("Reversed"),
    CANCELLED("Cancelled"),
    ON_HOLD("On Hold");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
