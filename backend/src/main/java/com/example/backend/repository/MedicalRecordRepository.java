package com.example.backend.repository;

import com.example.backend.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>,
                JpaSpecificationExecutor<MedicalRecord> {

        @Query("""
                            SELECT mr FROM MedicalRecord mr
                            LEFT JOIN FETCH mr.medications
                            LEFT JOIN FETCH mr.patient
                            LEFT JOIN FETCH mr.appointment a
                            LEFT JOIN FETCH a.timeSlot
                            WHERE mr.appointment.id = :appointmentId
                        """)
        Optional<MedicalRecord> findByAppointmentIdWithDetails(@Param("appointmentId") Long appointmentId);

        @Query("""
                        SELECT mr FROM MedicalRecord mr
                        LEFT JOIN FETCH mr.medications
                        WHERE mr.appointment.id = :appointmentId
                        """)
        Optional<MedicalRecord> findByAppointmentIdWithMedications(@Param("appointmentId") Long appointmentId);

        boolean existsByAppointmentId(Long appointmentId);

        @Query("""
                            SELECT mr FROM MedicalRecord mr
                            LEFT JOIN FETCH mr.medications
                            LEFT JOIN FETCH mr.appointment a
                            LEFT JOIN FETCH a.timeSlot
                            WHERE mr.patient.id = :patientId
                            ORDER BY mr.createdAt DESC
                        """)
        List<MedicalRecord> findPatientHistory(@Param("patientId") Long patientId);

}