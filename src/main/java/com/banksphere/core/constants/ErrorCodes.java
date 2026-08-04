package com.banksphere.core.constants;

public final class ErrorCodes {
    private ErrorCodes() {}

    public static final String AUTH_001 = "AUTH_INVALID_CREDENTIALS";
    public static final String AUTH_002 = "AUTH_TOKEN_EXPIRED";
    public static final String AUTH_003 = "AUTH_TOKEN_INVALID";
    public static final String AUTH_004 = "AUTH_ACCOUNT_LOCKED";

    public static final String ACCT_001 = "ACCT_NOT_FOUND";
    public static final String ACCT_002 = "ACCT_INSUFFICIENT_FUNDS";
    public static final String ACCT_003 = "ACCT_CLOSED";
    public static final String ACCT_004 = "ACCT_FROZEN";

    public static final String TXN_001 = "TXN_INVALID";
    public static final String TXN_002 = "TXN_LIMIT_EXCEEDED";
    public static final String TXN_003 = "TXN_DUPLICATE";

    public static final String LOAN_001 = "LOAN_NOT_ELIGIBLE";
    public static final String LOAN_002 = "LOAN_NOT_FOUND";

    public static final String FRAUD_001 = "FRAUD_SUSPECTED";

    public static final String SYS_001 = "SYS_INTERNAL_ERROR";
}
