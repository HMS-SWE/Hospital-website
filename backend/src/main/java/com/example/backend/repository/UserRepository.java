package com.example.backend.repository;

import com.example.backend.entity.User;
import com.example.backend.enums.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    boolean existsByUserName(String userName);

    Optional<User> findByEmail(String email);
}
