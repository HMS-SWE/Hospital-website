package com.example.backend.config;

import com.example.backend.entity.Appointment;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.MedicalRecord;
import com.example.backend.entity.Patient;
import com.example.backend.entity.Schedule;
import com.example.backend.entity.Specialty;
import com.example.backend.entity.TimeSlot;
import com.example.backend.entity.User;
import com.example.backend.enums.ChronicDisease;
import com.example.backend.enums.Gender;
import com.example.backend.enums.Role;
import com.example.backend.enums.TimeSlotStatus;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.MedicalRecordRepository;
import com.example.backend.repository.PatientRepository;
import com.example.backend.repository.ScheduleRepository;
import com.example.backend.repository.SpecialtyRepository;
import com.example.backend.repository.TimeSlotRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ScheduleRepository scheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByRole(Role.ADMIN) && specialtyRepository.count() > 0) {
            log.info("Database already seeded — skipping seed");
            return;
        }

        try {
            seedAdmin();

            List<Specialty> specialties = seedSpecialties();
            List<Doctor> doctors = seedDoctors(specialties);
            List<Patient> patients = seedPatients();
            List<Schedule> schedules = seedSchedules(doctors);
            List<TimeSlot> timeSlots = seedTimeSlots(schedules);
            seedAppointmentsAndMedicalRecords(patients, doctors, timeSlots);

            log.info("Initial database seeding completed successfully");
        } catch (Exception e) {
            log.warn("DataSeeder skipped: {}", e.getMessage());
        }
    }

    private void seedAdmin() {
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("Admin user exists already");
            return;
        }

        User admin = User.builder()
                .userName("admin")
                .fullName("System Admin")
                .email("admin@hospital.com")
                .password(passwordEncoder.encode("Admin@1234"))
                .isActive(true)
                .role(Role.ADMIN)
                .gender(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);
        log.info("Admin account created successfully");
    }

    private List<Specialty> seedSpecialties() {
        if (specialtyRepository.count() > 0) {
            log.info("Specialties already seeded");
            return specialtyRepository.findAll();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Specialty> specialties = List.of(
                Specialty.builder().name("Cardiology").location("Building A, Floor 3").build(),
                Specialty.builder().name("Neurology").location("Building B, Floor 2").build(),
                Specialty.builder().name("Pediatrics").location("Building C, Floor 1").build(),
                Specialty.builder().name("Orthopedics").location("Building D, Floor 4").build(),
                Specialty.builder().name("Dermatology").location("Building E, Floor 2").build()
        );

        return specialtyRepository.saveAll(specialties);
    }

    private List<Doctor> seedDoctors(List<Specialty> specialties) {
        if (doctorRepository.count() > 0) {
            log.info("Doctors already seeded");
            return doctorRepository.findAll();
        }

        Map<String, Specialty> specialtyMap = new HashMap<>();
        specialties.forEach(s -> specialtyMap.put(s.getName(), s));
        LocalDateTime now = LocalDateTime.now();

        List<Doctor> doctors = List.of(
                Doctor.builder()
                        .userName("drsmith")
                        .fullName("Dr. Lisa Smith")
                        .email("lisa.smith@hospital.com")
                        .password(passwordEncoder.encode("Doctor@123"))
                        .isActive(true)
                        .role(Role.DOCTOR)
                        .gender(Gender.FEMALE)
                        .birthDate(LocalDate.of(1981, 4, 15))
                        .phoneNumber("+201200000001")
                        .address("10 Cardio Lane")
                        .specialty(specialtyMap.get("Cardiology"))
                        .department("Cardiology")
                        .degree("MD")
                        .qualifications("Board Certified in Cardiology")
                        .licenseNumber("DOC-1001")
                        .examinationPrice(150f)
                        .consultationPrice(120f)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Doctor.builder()
                        .userName("drjohnson")
                        .fullName("Dr. Ahmed Johnson")
                        .email("ahmed.johnson@hospital.com")
                        .password(passwordEncoder.encode("Doctor@123"))
                        .isActive(true)
                        .role(Role.DOCTOR)
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.of(1978, 9, 28))
                        .phoneNumber("+201200000002")
                        .address("22 Neuro Avenue")
                        .specialty(specialtyMap.get("Neurology"))
                        .department("Neurology")
                        .degree("MD")
                        .qualifications("Board Certified in Neurology")
                        .licenseNumber("DOC-1002")
                        .examinationPrice(170f)
                        .consultationPrice(130f)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Doctor.builder()
                        .userName("drnguyen")
                        .fullName("Dr. Mai Nguyen")
                        .email("mai.nguyen@hospital.com")
                        .password(passwordEncoder.encode("Doctor@123"))
                        .isActive(true)
                        .role(Role.DOCTOR)
                        .gender(Gender.FEMALE)
                        .birthDate(LocalDate.of(1985, 11, 2))
                        .phoneNumber("+201200000003")
                        .address("31 Kids Health Blvd")
                        .specialty(specialtyMap.get("Pediatrics"))
                        .department("Pediatrics")
                        .degree("MD")
                        .qualifications("Board Certified in Pediatrics")
                        .licenseNumber("DOC-1003")
                        .examinationPrice(130f)
                        .consultationPrice(100f)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Doctor.builder()
                        .userName("drkhan")
                        .fullName("Dr. Omar Khan")
                        .email("omar.khan@hospital.com")
                        .password(passwordEncoder.encode("Doctor@123"))
                        .isActive(true)
                        .role(Role.DOCTOR)
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.of(1980, 2, 20))
                        .phoneNumber("+201200000004")
                        .address("44 Bone Care Road")
                        .specialty(specialtyMap.get("Orthopedics"))
                        .department("Orthopedics")
                        .degree("MD")
                        .qualifications("Board Certified in Orthopedics")
                        .licenseNumber("DOC-1004")
                        .examinationPrice(160f)
                        .consultationPrice(125f)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Doctor.builder()
                        .userName("drrebecca")
                        .fullName("Dr. Rebecca Al-Sayed")
                        .email("rebecca.alsayed@hospital.com")
                        .password(passwordEncoder.encode("Doctor@123"))
                        .isActive(true)
                        .role(Role.DOCTOR)
                        .gender(Gender.FEMALE)
                        .birthDate(LocalDate.of(1983, 7, 12))
                        .phoneNumber("+201200000005")
                        .address("55 Skin Wellness Street")
                        .specialty(specialtyMap.get("Dermatology"))
                        .department("Dermatology")
                        .degree("MD")
                        .qualifications("Board Certified in Dermatology")
                        .licenseNumber("DOC-1005")
                        .examinationPrice(140f)
                        .consultationPrice(115f)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        return doctorRepository.saveAll(doctors);
    }

    private List<Patient> seedPatients() {
        if (patientRepository.count() > 0) {
            log.info("Patients already seeded");
            return patientRepository.findAll();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Patient> patients = List.of(
                Patient.builder()
                        .userName("patient1")
                        .fullName("Sara Ahmed")
                        .email("sara.ahmed@hospital.com")
                        .password(passwordEncoder.encode("Patient@123"))
                        .isActive(true)
                        .role(Role.PATIENT)
                        .gender(Gender.FEMALE)
                        .birthDate(LocalDate.of(1990, 3, 25))
                        .phoneNumber("+201200000011")
                        .address("12 Nile Street")
                        .nationalId("NID-2001")
                        .emergencyNumber("+201200000111")
                        .whatsappNumber("+201200000112")
                        .bloodType("A+")
                        .diagnoses("Seasonal allergies")
                        .prescriptions("Antihistamine")
                        .currentMedication("Cetirizine 10mg")
                        .chronicDisease(ChronicDisease.IMMUNE_SYSTEM_DISEASE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Patient.builder()
                        .userName("patient2")
                        .fullName("Omar Mostafa")
                        .email("omar.mostafa@hospital.com")
                        .password(passwordEncoder.encode("Patient@123"))
                        .isActive(true)
                        .role(Role.PATIENT)
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.of(1988, 8, 5))
                        .phoneNumber("+201200000012")
                        .address("45 Garden Road")
                        .nationalId("NID-2002")
                        .emergencyNumber("+201200000121")
                        .whatsappNumber("+201200000122")
                        .bloodType("O-")
                        .diagnoses("High blood pressure")
                        .prescriptions("Beta-blocker")
                        .currentMedication("Atenolol 50mg")
                        .chronicDisease(ChronicDisease.BLOOD_PRESSURE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        return patientRepository.saveAll(patients);
    }

    private List<Schedule> seedSchedules(List<Doctor> doctors) {
        if (scheduleRepository.count() > 0) {
            log.info("Schedules already seeded");
            return scheduleRepository.findAll();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = new ArrayList<>();

        for (Doctor doctor : doctors) {
            schedules.add(Schedule.builder()
                    .doctor(doctor)
                    .dayOfWeek(DayOfWeek.MONDAY)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(13, 0))
                    .active(true)
                    .slotDurationMinutes(30)
                    .build());
            schedules.add(Schedule.builder()
                    .doctor(doctor)
                    .dayOfWeek(DayOfWeek.WEDNESDAY)
                    .startTime(LocalTime.of(14, 0))
                    .endTime(LocalTime.of(18, 0))
                    .active(true)
                    .slotDurationMinutes(30)
                    .build());
        }

        return scheduleRepository.saveAll(schedules);
    }

    private List<TimeSlot> seedTimeSlots(List<Schedule> schedules) {
        if (timeSlotRepository.count() > 0) {
            log.info("Time slots already seeded");
            return timeSlotRepository.findAll();
        }

        LocalDate baseDate = LocalDate.now().plusDays(1);
        List<TimeSlot> slots = new ArrayList<>();

        for (Schedule schedule : schedules) {
            LocalDate date = baseDate;
            for (int day = 0; day < 3; day++) {
                LocalTime start = schedule.getStartTime();
                while (start.isBefore(schedule.getEndTime())) {
                    LocalTime end = start.plusMinutes(schedule.getSlotDurationMinutes());
                    if (!end.isAfter(schedule.getEndTime())) {
                        slots.add(TimeSlot.builder()
                                .schedule(schedule)
                                .date(date)
                                .startTime(start)
                                .endTime(end)
                                .status(TimeSlotStatus.AVAILABLE)
                                .build());
                    }
                    start = end;
                }
                date = date.plusDays(1);
            }
        }

        return timeSlotRepository.saveAll(slots);
    }

    private void seedAppointmentsAndMedicalRecords(List<Patient> patients, List<Doctor> doctors, List<TimeSlot> timeSlots) {
        if (appointmentRepository.count() > 0) {
            log.info("Appointments already seeded");
            return;
        }

        if (patients.isEmpty() || doctors.isEmpty() || timeSlots.isEmpty()) {
            log.warn("Skipping appointment seeding because required records are missing");
            return;
        }

        Patient patient = patients.get(0);
        Doctor doctor = doctors.get(0);
        TimeSlot firstAvailable = timeSlots.stream()
                .filter(slot -> slot.getSchedule().getDoctor().getId().equals(doctor.getId()))
                .filter(slot -> slot.getStatus() == TimeSlotStatus.AVAILABLE)
                .findFirst()
                .orElse(null);

        if (firstAvailable == null) {
            log.warn("No available time slot found for appointment seeding");
            return;
        }

        firstAvailable.setStatus(TimeSlotStatus.BOOKED);
        timeSlotRepository.save(firstAvailable);

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .timeSlot(firstAvailable)
                .notes("Initial consultation for cardiac wellness.")
                .examinationPrice(doctor.getExaminationPrice())
                .build();

        appointmentRepository.save(appointment);

        MedicalRecord medicalRecord = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .appointment(appointment)
                .diagnosis("Mild hypertension")
                .prescription("Lifestyle changes and follow-up medication")
                .labResults("Normal cholesterol and glucose levels")
                .notes("Recommend regular exercise and low-sodium diet.")
                .startDate(LocalDate.now())
                .build();

        medicalRecordRepository.save(medicalRecord);
    }
}