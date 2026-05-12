package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medications", indexes = {
        @Index(name = "idx_medication_record", columnList = "medical_record_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_record_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_medication_record"))
    private MedicalRecord medicalRecord;

    @Column(nullable = false, length = 500)
    private String name;
}