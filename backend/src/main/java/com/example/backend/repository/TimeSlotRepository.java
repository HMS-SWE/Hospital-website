package com.example.backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.TimeSlot;
import com.example.backend.enums.TimeSlotStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

        // Pessimistic write lock
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT t FROM TimeSlot t WHERE t.id = :id")
        Optional<TimeSlot> findByIdWithLock(@Param("id") Long id);

        @Query("""
                        SELECT t FROM TimeSlot t
                        WHERE t.doctor.id = :doctorId
                          AND t.date = :date
                          AND t.status = 'AVAILABLE'
                        ORDER BY t.startTime
                        """)
        List<TimeSlot> findAvailableSlots(
                        @Param("doctorId") Long doctorId,
                        @Param("date") LocalDate date);

        // Checks if a slot already exists before generating (idempotent generation)
        boolean existsByScheduleIdAndDateAndStartTime(
                        Long scheduleId, LocalDate date, LocalTime startTime);

        @Query("""
                        SELECT t FROM TimeSlot t
                        WHERE t.doctor.id = :doctorId
                          AND t.date BETWEEN :from AND :to
                          AND t.status = :status
                        """)
        List<TimeSlot> findByDoctorAndDateRangeAndStatus(
                        @Param("doctorId") Long doctorId,
                        @Param("from") LocalDate from,
                        @Param("to") LocalDate to,
                        @Param("status") TimeSlotStatus status);
}