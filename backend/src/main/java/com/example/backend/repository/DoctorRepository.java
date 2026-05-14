package com.example.backend.repository;

import com.example.backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d JOIN FETCH d.specialty s " +
           "WHERE d.isActive = true AND " +
           "(LOWER(d.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Doctor> searchByNameOrSpecialty(@Param("query") String query);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.specialty WHERE d.isActive = true")
    List<Doctor> findAllActive();
}
