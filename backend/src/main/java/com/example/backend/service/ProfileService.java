package com.example.backend.service;

import com.example.backend.dto.doctor.response.DoctorPublicResponse;
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

    // ─── GET OWN PROFILE ─────────────────────────────────────────────────────

    public UserProfileResponse getUserProfile(Long userId) {
        return userMapper.toResponse(findUserById(userId));
    }

    public DoctorProfileResponse getDoctorProfile(Long userId) {
        return doctorMapper.toResponse(findDoctorByUserId(userId));
    }

    public PatientProfileResponse getPatientProfile(Long userId) {
        return patientMapper.toResponse(findPatientByUserId(userId));
    }

    // ─── GET PUBLIC DOCTOR PROFILE ────────────────────────────────────────────
    // used when a doctor or patient views another doctor

    public DoctorPublicResponse getDoctorPublicProfile(Long userId) {
        return doctorMapper.toPublicResponse(findDoctorByUserId(userId));
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileRequest request) {
        User user = findUserById(userId);
        userUpdateHelper.applyUpdates(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public DoctorProfileResponse updateDoctorProfile(Long userId, DoctorProfileRequest request) {
        Doctor doctor = findDoctorByUserId(userId);
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
        Patient patient = findPatientByUserId(userId);
        userUpdateHelper.applyUpdates(patient, request);
        patient.setEmergencyNumber(request.getEmergencyNumber());
        patient.setWhatsappNumber(request.getWhatsappNumber());
        patient.setBloodType(request.getBloodType());
        patient.setChronicDisease(request.getChronicDisease());
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    // ─── DELETE (soft) ────────────────────────────────────────────────────────

    @Transactional
    public void deactivateUser(Long targetUserId, Long requestingUserId) {
        // admin cannot delete themselves
        if (targetUserId.equals(requestingUserId)) {
            throw new RuntimeException("You cannot delete your own account");
        }

        User user = findUserById(targetUserId);
        user.setIsActive(false);
        userRepository.save(user);
    }

    // ─── PRIVATE FINDERS ─────────────────────────────────────────────────────

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Doctor findDoctorByUserId(Long userId) {
        return doctorRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
    }

    private Patient findPatientByUserId(Long userId) {
        return patientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
    }
}