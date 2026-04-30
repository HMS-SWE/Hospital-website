package com.example.backend.dto.specialty;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialtyRequest {
    private String name;
    private String location;
}
