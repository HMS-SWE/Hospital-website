package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors", indexes = {
        @Index(name = "idx_doctor_specialty", columnList = "specialty_id"),
        @Index(name = "idx_doctor_user", columnList = "user_id", unique = true)
})
@DiscriminatorValue("DOCTOR")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends User {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialty_id", nullable = false, foreignKey = @ForeignKey(name = "fk_doctor_specialty"))
    private Specialty specialty;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String degree;

    @Column(length = 255)
    private String qualifications;

    @Column(name = "license_number", length = 80)
    private String licenseNumber;

    @Column(name = "examination_price")
    private Float examinationPrice;

    @Column(name = "consultation_price")
    private Float consultationPrice;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();
}