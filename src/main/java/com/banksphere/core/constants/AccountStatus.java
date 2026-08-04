package com.banksphere.core.constants;

public enum AccountStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    FROZEN("Frozen"),
    CLOSED("Closed"),
    PENDING_VERIFICATION("Pending Verification"),
    DORMANT("Dormant");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
