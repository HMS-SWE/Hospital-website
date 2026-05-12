package com.example.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MedicationLinesValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMedicationLines {

    String message() default "Each medication must be on its own line and cannot be blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}