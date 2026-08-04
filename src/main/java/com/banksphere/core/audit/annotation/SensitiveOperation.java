package com.banksphere.core.audit.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveOperation {
    String value() default "";
    boolean requiresMfa() default false;
    boolean logParameters() default false;
}
