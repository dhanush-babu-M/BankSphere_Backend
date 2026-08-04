package com.banksphere.core.exception;

public class InvalidTransactionException extends BankSphereException {
    public InvalidTransactionException(String errorCode, String message) {
        super(errorCode, message);
    }

    public InvalidTransactionException(String message) {
        super(com.banksphere.core.constants.ErrorCodes.TXN_001, message);
    }
}
