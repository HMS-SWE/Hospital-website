package com.example.backend.dto.specialty;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialtyResponse {
    private Long id;
    private String name;
    private String location;
}