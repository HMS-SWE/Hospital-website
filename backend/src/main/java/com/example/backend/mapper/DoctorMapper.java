package com.example.backend.mapper;

import com.example.backend.dto.doctor.response.DoctorPublicResponse;
import com.example.backend.dto.profile.response.DoctorProfileResponse;
import com.example.backend.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public DoctorProfileResponse toResponse(Doctor doctor) {
        return DoctorProfileResponse.builder()
                .id(doctor.getId())
                .userName(doctor.getUserName())
                .fullName(doctor.getFullName())
                .email(doctor.getEmail())
                .role(doctor.getRole())
                .gender(doctor.getGender())
                .birthDate(doctor.getBirthDate())
                .phoneNumber(doctor.getPhoneNumber())
                .address(doctor.getAddress())
                .profilePicturePath(doctor.getProfilePicturePath())
                .createdAt(doctor.getCreatedAt())
                .specialtyName(doctor.getSpecialty().getName())
                .specialtyLocation(doctor.getSpecialty().getLocation())
                .department(doctor.getDepartment())
                .degree(doctor.getDegree())
                .qualifications(doctor.getQualifications())
                .licenseNumber(doctor.getLicenseNumber())
                .examinationPrice(doctor.getExaminationPrice())
                .build();
    }

    public DoctorPublicResponse toPublicResponse(Doctor doctor) {
        return DoctorPublicResponse.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .specialtyName(doctor.getSpecialty().getName())
                .specialtyLocation(doctor.getSpecialty().getLocation())
                .department(doctor.getDepartment())
                .degree(doctor.getDegree())
                .qualifications(doctor.getQualifications())
                .licenseNumber(doctor.getLicenseNumber())
                .examinationPrice(doctor.getExaminationPrice())
                .build();
    }
}