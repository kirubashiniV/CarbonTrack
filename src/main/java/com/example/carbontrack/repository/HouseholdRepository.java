package com.example.carbontrack.repository;

import com.example.carbontrack.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdRepository extends JpaRepository<Household, Long> {

    boolean existsByEmail(String email);
}