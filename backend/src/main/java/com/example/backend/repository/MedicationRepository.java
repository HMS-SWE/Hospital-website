package com.example.backend.repository;

import com.example.backend.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findByMedicalRecordId(Long medicalRecordId);
}