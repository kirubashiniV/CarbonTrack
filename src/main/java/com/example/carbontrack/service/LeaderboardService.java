package com.example.carbontrack.service;

import com.example.carbontrack.dto.LeaderboardEntry;
import com.example.carbontrack.entity.Household;
import com.example.carbontrack.repository.HouseholdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
public class LeaderboardService {

    private final HouseholdRepository householdRepository;
    private final FootprintService footprintService;

    public LeaderboardService(HouseholdRepository householdRepository, FootprintService footprintService) {
        this.householdRepository = householdRepository;
        this.footprintService = footprintService;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getLeaderboard(YearMonth month) {
        YearMonth previousMonth = month.minusMonths(1);
        return householdRepository.findAll().stream()
                .map(household -> entryFor(household, previousMonth, month))
                .sorted(Comparator.comparingDouble(LeaderboardEntry::percentReduction).reversed()
                        .thenComparing(LeaderboardEntry::householdName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(LeaderboardEntry::householdId))
                .toList();
    }

    private LeaderboardEntry entryFor(Household household, YearMonth previousMonth, YearMonth currentMonth) {
        double previous = footprintService.calculateMonthlyFootprint(household.getId(), previousMonth);
        double current = footprintService.calculateMonthlyFootprint(household.getId(), currentMonth);
        // A zero previous-month footprint is defined as zero reduction to avoid division by zero.
        double reduction = previous == 0.0 ? 0.0 : ((previous - current) / previous) * 100.0;

        return new LeaderboardEntry(
                household.getId(), household.getName(), household.getCity(), previous, current, reduction);
    }
}