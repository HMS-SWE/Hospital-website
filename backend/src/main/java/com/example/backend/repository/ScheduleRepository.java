package com.example.backend.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByDoctorIdAndIsActiveTrue(Long doctorId);

    List<Schedule> findByDoctorIdAndDayOfWeekAndIsActiveTrue(
            Long doctorId, DayOfWeek dayOfWeek);
}
