package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.enums.*;
import com.example.backend.repository.*;
import com.example.backend.dto.appointment.AppointmentResponse;
import com.example.backend.dto.appointment.DoctorAppointmentView;
import lombok.RequiredArgsConstructor;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PatientRepository patientRepository;
    private final CancellationRuleRepository cancellationRuleRepository;
    private final JwtService jwtService;

    @Transactional
    public Appointment bookAppointment(String token, Long slotId) {
        long userId = jwtService.extractUserId(token);
        Role role = jwtService.extractRole(token);
        if (role != Role.PATIENT) {
            throw new RuntimeException("Only patients can book appointments");
        }

        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        TimeSlot timeSlot = timeSlotRepository.findByIdForUpdate(slotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (patient.getAppointments().stream().anyMatch(a -> a.getTimeSlot().getDate().isEqual(timeSlot.getDate()) &&
                a.getTimeSlot().getStartTime().equals(timeSlot.getStartTime()))) {
            throw new RuntimeException("Patient already has an appointment at this time");

        }
        if (timeSlot.getStatus() != TimeSlotStatus.AVAILABLE) {
            throw new RuntimeException("Time slot is not available");
        }

        if (timeSlot.getDate().isBefore(java.time.LocalDate.now())
                || (timeSlot.getDate().isEqual(java.time.LocalDate.now())
                        && timeSlot.getStartTime().isBefore(java.time.LocalTime.now()))) {
            throw new RuntimeException("Cannot book past time slot");
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

    @Transactional
    public void cancelAppointment(String token, Long appointmentId, String reason) {
        long userId = jwtService.extractUserId(token);
        Role role = jwtService.extractRole(token);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (userId != appointment.getPatient().getId() && userId != appointment.getDoctor().getId()
                && role != Role.ADMIN) {
            throw new RuntimeException("Unauthorized");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Already cancelled");
        }

        TimeSlot slot = appointment.getTimeSlot();

        CancellationRule rule = cancellationRuleRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No cancellation rule found"));

        int hours = rule.getMinimumNoticeHours();

        java.time.LocalDateTime appointmentTime = java.time.LocalDateTime.of(slot.getDate(), slot.getStartTime());

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        long diffHours = java.time.Duration.between(now, appointmentTime).toHours();

        if (diffHours < hours) {
            throw new RuntimeException("Cannot cancel within " + hours + " hours");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelReason(reason);

        slot.setStatus(TimeSlotStatus.AVAILABLE);
        appointmentRepository.save(appointment);

        timeSlotRepository.save(slot);
    }

    @Transactional
    public void editAppointment(String token, Long appointmentId, Long newSlotId) {
        long userId = jwtService.extractUserId(token);
        Role role = jwtService.extractRole(token);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (role != Role.PATIENT) {
            throw new RuntimeException("Only patients can edit appointments");
        }
        if (userId != appointment.getPatient().getId()) {
            throw new RuntimeException("Unauthorized");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot edit cancelled appointment");
        }
        TimeSlot oldSlot = appointment.getTimeSlot();
        TimeSlot newSlot = timeSlotRepository.findByIdForUpdate(newSlotId)
                .orElseThrow(() -> new RuntimeException("New time slot not found"));
        if (oldSlot.getId() == newSlot.getId()) {
            throw new RuntimeException("Already booked in this time slot");
        }
        if (newSlot.getDate().isBefore(java.time.LocalDate.now())
                || (newSlot.getDate().isEqual(java.time.LocalDate.now())
                        && newSlot.getStartTime().isBefore(java.time.LocalTime.now()))) {
            throw new RuntimeException("Cannot book past time slot");
        }
        if (newSlot.getStatus() != TimeSlotStatus.AVAILABLE) {
            throw new RuntimeException("New time slot is not available");
        }
        Patient patient = appointment.getPatient();
        if (patient.getAppointments().stream().anyMatch(a -> a.getTimeSlot().getDate().isEqual(newSlot.getDate()) &&
                a.getTimeSlot().getStartTime().equals(newSlot.getStartTime()))) {
            throw new RuntimeException("Patient already has an appointment at this time");

        }
        oldSlot.setStatus(TimeSlotStatus.AVAILABLE);
        newSlot.setStatus(TimeSlotStatus.BOOKED);
        appointment.setTimeSlot(newSlot);

        timeSlotRepository.save(oldSlot);
        timeSlotRepository.save(newSlot);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public java.util.List<AppointmentResponse> getMyAppointments(String token) {
        long userId = jwtService.extractUserId(token);
        Role role = jwtService.extractRole(token);

        java.util.List<Appointment> appointments;

        if (role == Role.PATIENT) {
            appointments = appointmentRepository.findByPatientId(userId);
        } else if (role == Role.DOCTOR) {
            appointments = appointmentRepository.findByDoctorId(userId);
        } else {
            throw new RuntimeException("Unauthorized");
        }

        return appointments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setAppointmentId(appointment.getId());
        response.setDoctorName(appointment.getDoctor().getFullName());
        response.setPatientName(appointment.getPatient().getFullName());
        response.setDate(appointment.getTimeSlot().getDate().toString());
        response.setStartTime(appointment.getTimeSlot().getStartTime().toString());
        response.setEndTime(appointment.getTimeSlot().getEndTime().toString());
        response.setStatus(appointment.getStatus().name());
        response.setExaminationPrice(appointment.getExaminationPrice());
        return response;
    }

    // Egypt timezone — covers both EET (UTC+2) and EEST (UTC+3) automatically
    private static final ZoneId EGYPT_ZONE = ZoneId.of("Africa/Cairo");

    public List<DoctorAppointmentView> getTodaysAppointments(Long doctorId) {
        LocalDate today = LocalDate.now(EGYPT_ZONE); // ← timezone-aware
        return appointmentRepository.findTodaysAppointmentsForDoctor(
                doctorId,
                today,
                AppointmentStatus.CANCELLED);
    }
}