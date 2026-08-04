package com.banksphere.core.exception;

import com.banksphere.core.constants.ErrorCodes;
import java.time.LocalDateTime;

public class AccountLockedException extends BankSphereException {
    private final String accountNumber;
    private final String lockReason;
    private final LocalDateTime lockedUntil;

    public AccountLockedException(String accountNumber, String lockReason, LocalDateTime lockedUntil) {
        super(ErrorCodes.AUTH_004, "Account is locked: " + accountNumber);
        this.accountNumber = accountNumber;
        this.lockReason = lockReason;
        this.lockedUntil = lockedUntil;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getLockReason() {
        return lockReason;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }
}
