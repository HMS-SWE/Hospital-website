package com.example.backend.repository;

import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    // If the email exists it contains a User
    // If the email does not existit contains nothing
    Optional<User> findByEmail(String email);
}
