package com.example.backend.service;

import com.example.backend.dto.specialty.*;
import com.example.backend.entity.Specialty;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SpecialtyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Transactional
    public SpecialtyResponse create(SpecialtyRequest request) {
        if (specialtyRepository.existsByName(request.getName())) {
            throw new ConflictException(
                "Specialty with name '" + request.getName() + "' already exists"
            );
        }

        Specialty specialty = Specialty.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();

        return toResponse(specialtyRepository.save(specialty));
    }

    // ─── GET ALL ─────────────────────────────────────────────────────────────

    public List<SpecialtyResponse> getAll() {
        return specialtyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── GET ONE ─────────────────────────────────────────────────────────────

    public SpecialtyResponse getById(Long id) {
        return toResponse(findById(id));
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Transactional
    public SpecialtyResponse update(Long id, SpecialtyRequest request) {
        Specialty specialty = findById(id);

        // check duplicate name — but exclude self
        if (specialtyRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ConflictException(
                "Specialty with name '" + request.getName() + "' already exists"
            );
        }

        specialty.setName(request.getName());
        specialty.setLocation(request.getLocation());

        return toResponse(specialtyRepository.save(specialty));
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long id) {
        Specialty specialty = findById(id);

        int doctorCount = specialtyRepository.countDoctorsBySpecialtyId(id);
        if (doctorCount > 0) {
            throw new ConflictException(
                "Cannot delete specialty '" + specialty.getName() + "' — " +
                doctorCount + " doctor(s) are still assigned to it"
            );
        }

        specialtyRepository.delete(specialty);
    }

    // ─── PRIVATE ─────────────────────────────────────────────────────────────

    private Specialty findById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Specialty not found with id: " + id
                ));
    }

    private SpecialtyResponse toResponse(Specialty specialty) {
        return SpecialtyResponse.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .location(specialty.getLocation())
                .doctorCount(specialtyRepository.countDoctorsBySpecialtyId(specialty.getId()))
                .build();
    }
}