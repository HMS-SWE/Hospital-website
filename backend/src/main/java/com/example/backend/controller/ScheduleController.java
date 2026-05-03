package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.example.backend.dto.*;
import com.example.backend.service.ScheduleService;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
   private final ScheduleService scheduleService;

    @GetMapping("/doctor/{doctorId}/available-days")
    public ResponseEntity<?> getAvailableDays(@PathVariable Long doctorId) {
        try {
            List<DayResponse> availableDays = scheduleService.getAvailableDaysForDoctor(doctorId);
            return ResponseEntity.ok(availableDays);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/doctor/{doctorId}/available-slots")
    public ResponseEntity<?> getAvailableSlots(@PathVariable Long doctorId, @RequestParam String date) {
        try {
            List<SlotResponse> availableSlots = scheduleService.getAvailableSlotsForDoctorAndDate(doctorId, java.time.LocalDate.parse(date));
            return ResponseEntity.ok(availableSlots);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
