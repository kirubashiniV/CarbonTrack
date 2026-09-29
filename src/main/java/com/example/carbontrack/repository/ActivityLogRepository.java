package com.example.carbontrack.repository;

import com.example.carbontrack.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByHousehold_IdOrderByLogDateDescIdDesc(Long householdId);

    List<ActivityLog> findByHousehold_IdAndLogDateBetween(
            Long householdId, LocalDate startDate, LocalDate endDate);

    void deleteByHousehold_Id(Long householdId);
}