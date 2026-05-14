package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_records", indexes = {
        @Index(name = "idx_mr_patient", columnList = "patient_id"),
        @Index(name = "idx_mr_doctor", columnList = "doctor_id"),
        @Index(name = "idx_mr_appointment", columnList = "appointment_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_mr_patient"))
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_mr_doctor"))
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id",
            foreignKey = @ForeignKey(name = "fk_mr_appointment"))
    private Appointment appointment;

    @Column(length = 1000)
    private String diagnosis;

    @Column(name = "treatment_plan", length = 1000)
    private String treatmentPlan;

    @Column(length = 1000)
    private String prescription;

    @Column(name = "lab_results", length = 1000)
    private String labResults;

    @Column(length = 1000)
    private String notes;

    @Column(name = "start_date")
    private LocalDate startDate;

    @OneToMany(mappedBy = "medicalRecord", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Medication> medications = new ArrayList<>();
}