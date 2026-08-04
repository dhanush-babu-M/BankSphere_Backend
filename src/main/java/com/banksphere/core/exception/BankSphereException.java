package com.banksphere.core.exception;

public class BankSphereException extends RuntimeException {
    private final String errorCode;
    private final Object details;

    public BankSphereException(String message) {
        super(message);
        this.errorCode = null;
        this.details = null;
    }

    public BankSphereException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public BankSphereException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getDetails() {
        return details;
    }
}
