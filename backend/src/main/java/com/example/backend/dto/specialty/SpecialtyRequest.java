// SpecialtyRequest.java
package com.example.backend.dto.specialty;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyRequest {

    @NotBlank(message = "Specialty name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9 ]+$",
        message = "Name cannot contain special characters"
    )
    private String name;

    @NotBlank(message = "Location is required")
    private String location;
}