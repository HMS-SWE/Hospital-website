package com.example.backend.dto.profile.request;

import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileRequest extends UserProfileRequest {

    @NotNull(message = "Specialty is required")
    private Long specialtyId;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Degree is required")
    private String degree;

    private String qualifications;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "Examination price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Price is unrealistically high")
    private Float examinationPrice;
}
