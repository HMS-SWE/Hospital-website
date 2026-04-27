package com.example.backend.dto.profile.response;

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
public class DoctorProfileResponse extends UserProfileResponse {
    private String specialtyName;
    private String department;
    private String degree;
    private String qualifications;
    private String licenseNumber;
    private Float examinationPrice;
    private Float consultationPrice;
}
