package com.banksphere.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IFSCValidator implements ConstraintValidator<ValidIFSC, String> {

    private static final Pattern IFSC_PATTERN = Pattern.compile("[A-Z]{4}0[A-Z0-9]{6}");

    @Override
    public void initialize(ValidIFSC constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(String ifsc, ConstraintValidatorContext context) {
        if (ifsc == null) {
            return false;
        }
        return IFSC_PATTERN.matcher(ifsc).matches();
    }
}
