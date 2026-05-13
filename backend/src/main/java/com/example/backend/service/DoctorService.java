package com.example.backend.service;

import com.example.backend.dto.doctor.DoctorSearchResponse;
import com.example.backend.entity.Doctor;
import com.example.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<DoctorSearchResponse> searchDoctors(String query) {
        List<Doctor> doctors;

        if (query == null || query.trim().isEmpty()) {
            doctors = doctorRepository.findAllActive();
        } else {
            doctors = doctorRepository.searchByNameOrSpecialty(query.trim());
        }

        return doctors.stream()
                .map(this::toResponse)
                .toList();
    }

    private DoctorSearchResponse toResponse(Doctor doctor) {
        return new DoctorSearchResponse(
                doctor.getId(),
                doctor.getFullName(),
                doctor.getSpecialty().getName(),
                doctor.getExaminationPrice(),
                doctor.getDepartment(),
                doctor.getDegree()
        );
    }
}
