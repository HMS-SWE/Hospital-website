package com.example.backend.controller;

import com.example.backend.dto.doctor.DoctorSearchResponse;
import com.example.backend.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorSearchController {

    private final DoctorService doctorService;

    @GetMapping("/search")
    public ResponseEntity<List<DoctorSearchResponse>> searchDoctors(
            @RequestParam(value = "query", defaultValue = "") String query) {
        return ResponseEntity.ok(doctorService.searchDoctors(query));
    }
}
