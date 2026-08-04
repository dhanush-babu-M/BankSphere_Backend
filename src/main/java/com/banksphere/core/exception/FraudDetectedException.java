package com.banksphere.core.exception;

import com.banksphere.core.constants.ErrorCodes;

public class FraudDetectedException extends BankSphereException {
    private final String transactionId;
    private final Double fraudScore;
    private final String reason;

    public FraudDetectedException(String transactionId, Double fraudScore, String reason) {
        super(ErrorCodes.FRAUD_001, "Fraud suspected for transaction: " + transactionId);
        this.transactionId = transactionId;
        this.fraudScore = fraudScore;
        this.reason = reason;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Double getFraudScore() {
        return fraudScore;
    }

    public String getReason() {
        return reason;
    }
}
