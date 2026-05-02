package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.CancellationRule;


public interface CancellationRuleRepository extends JpaRepository<CancellationRule, Long> {

}
