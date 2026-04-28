package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.enums.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository; 
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

      @Transactional
      public Appointment bookAppointment(Long patientId, Long slotId) {
            Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        TimeSlot timeSlot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (timeSlot.getStatus() != TimeSlotStatus.AVAILABLE) {
            throw new RuntimeException("Time slot is not available");
        }
        Doctor doctor = timeSlot.getSchedule().getDoctor();

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setExaminationPrice(doctor.getExaminationPrice());
        timeSlot.setStatus(TimeSlotStatus.BOOKED);
        timeSlotRepository.save(timeSlot);

        return appointmentRepository.save(appointment);
}
}
