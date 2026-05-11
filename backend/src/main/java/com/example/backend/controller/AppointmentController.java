package com.example.backend.controller;

import com.example.backend.dto.BookingRequest;
import com.example.backend.entity.Appointment;
import com.example.backend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.dto.CancelRequest;
import com.example.backend.dto.EditRequest;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Appointment appointment = appointmentService.bookAppointment(token, request.getSlotId());

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelAppointment(@RequestBody CancelRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {

            long id = request.getAppointmentId();
            String token = authHeader.substring(7);

            appointmentService.cancelAppointment(token, id, request.getReason());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editAppointment(@RequestBody EditRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            long appointmentId = request.getAppointmentId();
            long newSlotId = request.getNewSlotId();
            String token = authHeader.substring(7);

            appointmentService.editAppointment(token, appointmentId, newSlotId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/myAppointments")
    public ResponseEntity<?> getMyAppointments(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            return ResponseEntity.ok(appointmentService.getMyAppointments(token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}