package com.banksphere.core.exception;

import com.banksphere.core.constants.ErrorCodes;

public class DuplicateResourceException extends BankSphereException {
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(ErrorCodes.TXN_003, resourceName + " already exists with " + fieldName + " : " + fieldValue);
    }
}
