package com.example.backend.dto.appointment;

import com.example.backend.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

// AllArgsConstructor is required — JPQL maps constructor directly
@Getter
@AllArgsConstructor
public class DoctorAppointmentView {
    private Long appointmentId;
    private String patientFullName;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String notes;
    private Float examinationPrice;
}