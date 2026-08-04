package com.banksphere.core.constants;

public enum TransactionType {
    CREDIT("Credit", false),
    DEBIT("Debit", true),
    TRANSFER_IN("Transfer In", false),
    TRANSFER_OUT("Transfer Out", true),
    NEFT("NEFT Transfer", true),
    RTGS("RTGS Transfer", true),
    IMPS("IMPS Transfer", true),
    UPI("UPI Transfer", true),
    ATM_WITHDRAWAL("ATM Withdrawal", true),
    CASH_DEPOSIT("Cash Deposit", false),
    INTEREST_CREDIT("Interest Credit", false),
    EMI_DEDUCTION("EMI Deduction", true),
    FD_CREATION("FD Creation", true),
    FD_MATURITY("FD Maturity", false),
    CARD_PAYMENT("Card Payment", true),
    BILL_PAYMENT("Bill Payment", true),
    REFUND("Refund", false),
    CHARGE("Bank Charge", true);

    private final String description;
    private final boolean isDebit;

    TransactionType(String description, boolean isDebit) {
        this.description = description;
        this.isDebit = isDebit;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDebit() {
        return isDebit;
    }
}
