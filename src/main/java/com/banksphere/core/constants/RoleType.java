package com.banksphere.core.constants;

public enum RoleType {
    ROLE_ADMIN("Administrator", 1),
    ROLE_EMPLOYEE("Employee", 2),
    ROLE_CUSTOMER("Customer", 3),
    ROLE_AUDITOR("Auditor", 4);

    private final String description;
    private final int level;

    RoleType(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }
}
