package com.example.backend.repository;

import com.example.backend.entity.Doctor;
import com.example.backend.entity.TimeSlot;
import com.example.backend.enums.TimeSlotStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.Optional;


public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
   

   List<TimeSlot> findBySchedule_Doctor_IdAndDateAndStatusOrderByStartTimeAsc(Long doctorId, LocalDate date, TimeSlotStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TimeSlot t WHERE t.id = :id")
    Optional<TimeSlot> findByIdForUpdate(Long id);
}
