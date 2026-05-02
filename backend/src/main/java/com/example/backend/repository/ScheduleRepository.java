 package com.example.backend.repository;
import com.example.backend.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
      
    List<Schedule> findByDoctorId(Long doctorId);
}