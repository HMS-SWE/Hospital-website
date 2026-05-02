package com.example.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;':\",.<>/?`~\\\\";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return true;
        }

        if (password.length() < 8) {
            return violation(context, "Password must be at least 8 characters long");
        }

        if (!password.chars().anyMatch(Character::isUpperCase)) {
            return violation(context, "Password must contain at least one uppercase letter");
        }

        if (!password.chars().anyMatch(Character::isLowerCase)) {
            return violation(context, "Password must contain at least one lowercase letter");
        }

        if (!password.chars().anyMatch(Character::isDigit)) {
            return violation(context, "Password must contain at least one number");
        }

        if (password.chars().noneMatch(ch -> SPECIAL_CHARACTERS.indexOf(ch) >= 0)) {
            return violation(context, "Password must contain at least one special character");
        }

        return true;
    }

    private boolean violation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
