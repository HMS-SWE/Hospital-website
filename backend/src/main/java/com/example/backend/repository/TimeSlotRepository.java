package com.example.backend.repository;

import com.example.backend.entity.Doctor;
import com.example.backend.entity.TimeSlot;
import com.example.backend.enums.TimeSlotStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
   
    List<TimeSlot> findBySchedule_Doctor_IdAndDateAndStatus(Long doctorId, LocalDate date, TimeSlotStatus status);
    List<TimeSlot> findById(String timeSlotId);
}
