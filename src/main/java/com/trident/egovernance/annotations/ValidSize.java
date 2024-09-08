package com.trident.egovernance.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SizeValidator.class)
public @interface ValidSize {
    String message() default "Invalid size";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
