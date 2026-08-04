package com.banksphere.core.exception;

import com.banksphere.core.constants.ErrorCodes;
import java.math.BigDecimal;

public class InsufficientFundsException extends BankSphereException {
    private final String accountNumber;
    private final BigDecimal availableBalance;
    private final BigDecimal requestedAmount;

    public InsufficientFundsException(String accountNumber, BigDecimal availableBalance, BigDecimal requestedAmount) {
        super(ErrorCodes.ACCT_002, "Insufficient funds in account: " + accountNumber);
        this.accountNumber = accountNumber;
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

    public InsufficientFundsException(String message) {
        super(ErrorCodes.ACCT_002, message);
        this.accountNumber = null;
        this.availableBalance = null;
        this.requestedAmount = null;
    }

    public InsufficientFundsException() {
        super(ErrorCodes.ACCT_002, "Insufficient funds");
        this.accountNumber = null;
        this.availableBalance = null;
        this.requestedAmount = null;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
}
