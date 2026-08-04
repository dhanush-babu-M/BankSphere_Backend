package com.banksphere.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PanCardValidator implements ConstraintValidator<ValidPanCard, String> {

    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

    @Override
    public void initialize(ValidPanCard constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(String panCard, ConstraintValidatorContext context) {
        if (panCard == null) {
            return false;
        }
        return PAN_PATTERN.matcher(panCard).matches();
    }
}
