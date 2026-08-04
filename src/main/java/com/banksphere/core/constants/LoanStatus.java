package com.banksphere.core.constants;

public enum LoanStatus {
    APPLIED("Applied"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    DISBURSED("Disbursed"),
    ACTIVE("Active"),
    CLOSED("Closed"),
    DEFAULT("Default"),
    NPA("Non-Performing Asset");

    private final String description;

    LoanStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
