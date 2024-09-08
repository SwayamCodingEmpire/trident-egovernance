package com.trident.egovernance.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.core.env.Environment;

public class SizeValidator implements ConstraintValidator<ValidSize,String> {
    private final Environment env;

    public SizeValidator(Environment env) {
        this.env = env;
    }

    private int minSize;
    private int maxSize;

    @Override
    public void initialize(ValidSize constraintAnnotation) {
        minSize = Integer.parseInt(env.getProperty("validation.size.min"));
        maxSize = Integer.parseInt(env.getProperty("validation.size.max"));
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null) return false;
        return s.length() >= minSize && s.length() <= maxSize;
    }
}
