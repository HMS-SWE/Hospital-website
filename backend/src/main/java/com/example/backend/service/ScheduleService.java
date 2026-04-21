package com.example.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.ScheduleRepository;
import com.example.backend.repository.TimeSlotRepository;

@Service
@Transactional(readOnly = true)
public class ScheduleService {
    @Autowired
    private final ScheduleRepository scheduleRepository;
    @Autowired
    private final TimeSlotRepository timeSlotRepository;
    @Autowired
    private final DoctorRepository doctorRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, TimeSlotRepository timeSlotRepository,
            DoctorRepository doctorRepository) {
        this.scheduleRepository = scheduleRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.doctorRepository = doctorRepository;
    }

}