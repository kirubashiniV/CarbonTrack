package com.example.carbontrack.service;

import com.example.carbontrack.dto.ActivityLogRequest;
import com.example.carbontrack.entity.ActivityLog;
import com.example.carbontrack.entity.Household;
import com.example.carbontrack.exception.ResourceNotFoundException;
import com.example.carbontrack.repository.ActivityLogRepository;
import com.example.carbontrack.repository.HouseholdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final HouseholdRepository householdRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository, HouseholdRepository householdRepository) {
        this.activityLogRepository = activityLogRepository;
        this.householdRepository = householdRepository;
    }

    @Transactional
    public ActivityLog createActivity(Long householdId, ActivityLogRequest request) {
        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new ResourceNotFoundException("Household " + householdId + " was not found"));
        if (!Double.isFinite(request.value())) {
            throw new IllegalArgumentException("Activity value must be a finite number");
        }

        ActivityLog activityLog = new ActivityLog();
        activityLog.setHousehold(household);
        activityLog.setActivityType(request.activityType());
        activityLog.setValue(request.value());
        activityLog.setLogDate(request.logDate());
        return activityLogRepository.save(activityLog);
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getActivities(Long householdId) {
        if (!householdRepository.existsById(householdId)) {
            throw new ResourceNotFoundException("Household " + householdId + " was not found");
        }
        return activityLogRepository.findByHousehold_IdOrderByLogDateDescIdDesc(householdId);
    }
}