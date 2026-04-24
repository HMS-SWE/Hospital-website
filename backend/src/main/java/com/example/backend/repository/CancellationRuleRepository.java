package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.CancellationRule;

@Repository
public interface CancellationRuleRepository extends JpaRepository<CancellationRule, Long> {

    @Query("SELECT c FROM CancellationRule c ORDER BY c.updatedAt DESC LIMIT 1")
    Optional<CancellationRule> findLatestRule();
}
