package com.banksphere.core.exception;

import com.banksphere.core.constants.ErrorCodes;

public class UnauthorizedAccessException extends BankSphereException {
    public UnauthorizedAccessException(String message) {
        super(ErrorCodes.AUTH_001, message);
    }
}
