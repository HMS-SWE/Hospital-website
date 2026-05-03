package com.example.backend.entity;

import com.example.backend.enums.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_patient_user", columnList = "user_id", unique = true)
})
@DiscriminatorValue("PATIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Patient extends User {

    @Column(name = "national_id", length = 30, unique = true)
    private String nationalId;

    @Column(name = "emergency_number", length = 20)
    private String emergencyNumber;

    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(length = 500)
    private String diagnoses;

    @Column(length = 1000)
    private String prescriptions;

    @Column(name = "current_medication", length = 500)
    private String currentMedication;

    @Enumerated(EnumType.STRING)
    @Column(name = "chronic_disease", length = 40)
    private ChronicDisease chronicDisease;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

}