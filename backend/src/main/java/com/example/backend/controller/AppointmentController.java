package com.example.backend.controller;
import com.example.backend.dto.BookingRequest;
import com.example.backend.entity.Appointment;
import com.example.backend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<Appointment> bookAppointment(@RequestBody BookingRequest request) {
        try {
            Appointment appointment = appointmentService.bookAppointment(request.getPatientId(), request.getSlotId());
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}