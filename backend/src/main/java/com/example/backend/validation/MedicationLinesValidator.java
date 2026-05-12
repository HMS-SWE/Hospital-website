package com.example.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MedicationLinesValidator implements ConstraintValidator<ValidMedicationLines, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            // @NotBlank on the field handles the empty case
            return true;
        }

        String[] lines = value.split("\\r?\\n", -1);

        for (String line : lines) {
            if (line.isBlank()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Each line must contain a medication name — remove empty lines").addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}