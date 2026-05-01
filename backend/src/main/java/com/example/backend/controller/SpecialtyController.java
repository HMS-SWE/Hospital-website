package com.example.backend.controller;

import com.example.backend.dto.specialty.*;
import com.example.backend.enums.Role;
import com.example.backend.security.AuthenticatedUserRequestAttributes;
import com.example.backend.service.SpecialtyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> getAll() {
        return ResponseEntity.ok(specialtyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(specialtyService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(
            @Valid @RequestBody SpecialtyRequest request,
            HttpServletRequest httpRequest) {

        requireAdmin(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(specialtyService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SpecialtyRequest request,
            HttpServletRequest httpRequest) {

        requireAdmin(httpRequest);
        return ResponseEntity.ok(specialtyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        requireAdmin(httpRequest);
        specialtyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void requireAdmin(HttpServletRequest request) {
        Role role = (Role) request.getAttribute(AuthenticatedUserRequestAttributes.USER_ROLE);
        if (role == null || role != Role.ADMIN) {
            throw new RuntimeException("Forbidden");
        }
    }
}