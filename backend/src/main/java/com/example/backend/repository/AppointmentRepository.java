package com.example.backend.repository;

import com.example.backend.entity.Appointment;
import com.example.backend.enums.AppointmentStatus;
import com.example.backend.dto.appointment.DoctorAppointmentView;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        List<Appointment> findByPatientId(Long patientId);

        List<Appointment> findByDoctorId(Long doctorId);

        // added cancelled as an argument to the query to make reusable
        // incase we wanted to exclude any other types of appointments from the results
        @Query("""
                            SELECT new com.example.backend.dto.appointment.DoctorAppointmentView(
                                a.id,
                                p.fullName,
                                ts.startTime,
                                ts.endTime,
                                a.status,
                                a.notes,
                                a.examinationPrice
                            )
                            FROM Appointment a
                            JOIN a.timeSlot ts
                            JOIN a.patient p
                            WHERE a.doctor.id = :doctorId
                              AND ts.date     = :today
                              AND a.status   != :cancelled
                            ORDER BY ts.startTime ASC
                        """)
        List<DoctorAppointmentView> findTodaysAppointmentsForDoctor(
                        @Param("doctorId") Long doctorId,
                        @Param("today") LocalDate today,
                        @Param("cancelled") AppointmentStatus cancelled);

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

        @Query("""
                            SELECT COUNT(a) > 0 FROM Appointment a
                            WHERE a.doctor.id = :doctorId
                            AND a.patient.id = :patientId
                        """)
        boolean existsByDoctorIdAndPatientId(
                        @Param("doctorId") Long doctorId,
                        @Param("patientId") Long patientId);
}
