package com.banksphere.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = IFSCValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ValidIFSC {

    String message() default "Invalid IFSC code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
