package com.example.backend.service;

import com.example.backend.dto.profile.request.*;
import com.example.backend.dto.profile.response.*;
import com.example.backend.entity.*;
import com.example.backend.mapper.*;
import com.example.backend.repository.*;
import com.example.backend.service.shared.UserUpdateHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;

    private final UserUpdateHelper userUpdateHelper;

    // ─── GET ─────────────────────────────────────────────────

    public UserProfileResponse getUserProfile(Long userId) {
        return userMapper.toResponse(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public DoctorProfileResponse getDoctorProfile(Long userId) {
        return doctorMapper.toResponse(doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found")));
    }

    public PatientProfileResponse getPatientProfile(Long userId) {
        return patientMapper.toResponse(patientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found")));
    }

    // ─── UPDATE ──────────────────────────────────────────────

    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userUpdateHelper.applyUpdates(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public DoctorProfileResponse updateDoctorProfile(Long userId, DoctorProfileRequest request) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        userUpdateHelper.applyUpdates(doctor, request);
        doctor.setDepartment(request.getDepartment());
        doctor.setDegree(request.getDegree());
        doctor.setQualifications(request.getQualifications());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setExaminationPrice(request.getExaminationPrice());
        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Transactional
    public PatientProfileResponse updatePatientProfile(Long userId, PatientProfileRequest request) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        userUpdateHelper.applyUpdates(patient, request);
        patient.setEmergencyNumber(request.getEmergencyNumber());
        patient.setWhatsappNumber(request.getWhatsappNumber());
        patient.setBloodType(request.getBloodType());
        patient.setChronicDisease(request.getChronicDisease());
        return patientMapper.toResponse(patientRepository.save(patient));
    }

}