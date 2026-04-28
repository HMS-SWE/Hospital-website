package com.example.backend.mapper;

import com.example.backend.dto.profile.response.PatientProfileResponse;
import com.example.backend.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientProfileResponse toResponse(Patient patient) {
        return PatientProfileResponse.builder()
                .id(patient.getId())
                .userName(patient.getUserName())
                .fullName(patient.getFullName())
                .email(patient.getEmail())
                .role(patient.getRole())
                .gender(patient.getGender())
                .birthDate(patient.getBirthDate())
                .phoneNumber(patient.getPhoneNumber())
                .address(patient.getAddress())
                .profilePicturePath(patient.getProfilePicturePath())
                .createdAt(patient.getCreatedAt())
                .emergencyNumber(patient.getEmergencyNumber())
                .whatsappNumber(patient.getWhatsappNumber())
                .bloodType(patient.getBloodType())
                .chronicDisease(patient.getChronicDisease())
                .build();
    }
}