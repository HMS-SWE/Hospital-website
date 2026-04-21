package com.example.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.Appointment;
import com.example.backend.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        boolean existsByTimeSlotIdAndPatientIdAndStatusNot(
                        Long timeSlotId, Long patientId, AppointmentStatus status);

        @Query("""
                        SELECT a FROM Appointment a
                        JOIN FETCH a.timeSlot t
                        JOIN FETCH a.doctor d
                        WHERE a.patient.id = :patientId
                        ORDER BY a.dateTime DESC
                        """)
        List<Appointment> findFullHistoryByPatient(@Param("patientId") Long patientId);

        @Query("""
                        SELECT a FROM Appointment a
                        WHERE a.doctor.id = :doctorId
                          AND a.date = :date
                          AND a.status = 'CONFIRMED'
                        ORDER BY a.dateTime
                        """)
        List<Appointment> findDoctorDailyAppointments(
                        @Param("doctorId") Long doctorId,
                        @Param("date") LocalDate date);

}