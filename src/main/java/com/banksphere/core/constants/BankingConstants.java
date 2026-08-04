package com.banksphere.core.constants;

import java.math.BigDecimal;

public final class BankingConstants {
    private BankingConstants() {}

    public static final BigDecimal MIN_TRANSFER_AMOUNT = new BigDecimal("1.00");
    public static final BigDecimal MAX_DAILY_TRANSFER = new BigDecimal("1000000.00");
    public static final String DEFAULT_CURRENCY = "INR";
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;
    public static final BigDecimal OVERDRAFT_INTEREST_RATE = new BigDecimal("0.18");
    public static final BigDecimal FD_MIN_AMOUNT = new BigDecimal("1000.00");
    public static final BigDecimal FD_MAX_AMOUNT = new BigDecimal("10000000.00");
    public static final BigDecimal CREDIT_CARD_MIN_PAYMENT_PERCENT = new BigDecimal("0.05");
    public static final BigDecimal LOAN_PROCESSING_FEE_PERCENT = new BigDecimal("0.01");
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String BEARER_PREFIX = "Bearer ";
}
