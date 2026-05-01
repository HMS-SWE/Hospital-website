package com.example.backend.repository;

import com.example.backend.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.specialty.id = :specialtyId")
    int countDoctorsBySpecialtyId(Long specialtyId);
}
