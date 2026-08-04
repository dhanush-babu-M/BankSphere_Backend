package com.banksphere.core.exception;

import com.banksphere.core.constants.ErrorCodes;

public class ResourceNotFoundException extends BankSphereException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(ErrorCodes.ACCT_001, resourceName + " not found with " + fieldName + " : " + fieldValue);
    }
}
