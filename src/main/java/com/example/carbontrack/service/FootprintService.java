package com.example.carbontrack.service;

import com.example.carbontrack.entity.ActivityLog;
import com.example.carbontrack.exception.ResourceNotFoundException;
import com.example.carbontrack.repository.ActivityLogRepository;
import com.example.carbontrack.repository.HouseholdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FootprintService {

    private final ActivityLogRepository activityLogRepository;
    private final HouseholdRepository householdRepository;
    private final EmissionFactorService emissionFactorService;

    public FootprintService(ActivityLogRepository activityLogRepository,
                            HouseholdRepository householdRepository,
                            EmissionFactorService emissionFactorService) {
        this.activityLogRepository = activityLogRepository;
        this.householdRepository = householdRepository;
        this.emissionFactorService = emissionFactorService;
    }

    @Transactional(readOnly = true)
    public double calculateMonthlyFootprint(Long householdId, YearMonth month) {
        requireHousehold(householdId);
        LocalDate firstDay = month.atDay(1);
        LocalDate lastDay = month.atEndOfMonth();

        return activityLogRepository.findByHousehold_IdAndLogDateBetween(householdId, firstDay, lastDay)
                .stream()
                .mapToDouble(log -> log.getValue() * emissionFactorService.getFactor(log.getActivityType()))
                .sum();
    }

    @Transactional(readOnly = true)
    public Map<YearMonth, Double> getFootprintHistory(Long householdId, int months) {
        requireHousehold(householdId);
        if (months < 1) {
            throw new IllegalArgumentException("months must be at least 1");
        }

        YearMonth currentMonth = YearMonth.now();
        YearMonth firstMonth = currentMonth.minusMonths(months - 1L);
        Map<YearMonth, Double> history = new LinkedHashMap<>();
        for (int offset = 0; offset < months; offset++) {
            YearMonth month = firstMonth.plusMonths(offset);
            history.put(month, calculateMonthlyFootprint(householdId, month));
        }
        return history;
    }

    private void requireHousehold(Long householdId) {
        if (!householdRepository.existsById(householdId)) {
            throw new ResourceNotFoundException("Household " + householdId + " was not found");
        }
    }
}