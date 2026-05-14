package com.example.backend.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSearchResponse {
    private Long id;
    private String fullName;
    private String specialtyName;
    private Float examinationPrice;
    private String department;
    private String degree;
}
