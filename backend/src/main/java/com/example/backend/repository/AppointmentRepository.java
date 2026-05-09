package com.example.backend.repository;

import com.example.backend.entity.Appointment;
import com.example.backend.enums.AppointmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  List<Appointment> findByPatientId(Long patientId);

  List<Appointment> findByDoctorId(Long doctorId);

  @Query("""
          SELECT a FROM Appointment a
          JOIN FETCH a.timeSlot ts
          WHERE a.doctor.id = :doctorId
          AND ts.date = :date
          AND a.status IN :statuses
      """)
  List<Appointment> findCancellableAppointments(
      @Param("doctorId") Long doctorId,
      @Param("date") LocalDate date,
      @Param("statuses") List<AppointmentStatus> statuses);

  @Modifying
  @Query("""
          UPDATE Appointment a
          SET a.status = :newStatus
          WHERE a.id IN :ids
      """)
  int bulkUpdateStatus(
      @Param("ids") List<Long> ids,
      @Param("newStatus") AppointmentStatus newStatus);
}
