package com.example.backend.dto.doctor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorPublicResponse {
    private Long id;
    private String fullName;
    private String specialtyName;
    private String specialtyLocation;
    private String department;
    private String degree;
    private String qualifications;
    private Float examinationPrice;
    private Float consultationPrice;
    private String licenseNumber;
}
