package com.banksphere.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountNumberValidator implements ConstraintValidator<ValidAccountNumber, String> {

    private static final String PREFIX = "BSP";

    @Override
    public void initialize(ValidAccountNumber constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(String accountNumber, ConstraintValidatorContext context) {
        if (accountNumber == null) {
            return false;
        }
        if (accountNumber.length() != 16) {
            return false;
        }
        return accountNumber.startsWith(PREFIX);
    }
}
