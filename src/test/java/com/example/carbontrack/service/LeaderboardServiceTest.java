package com.example.carbontrack.service;

import com.example.carbontrack.dto.LeaderboardEntry;
import com.example.carbontrack.entity.Household;
import com.example.carbontrack.repository.HouseholdRepository;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeaderboardServiceTest {

    @Test
    void ranksByPercentageReductionAndTreatsZeroBaselineAsZeroReduction() {
        HouseholdRepository householdRepository = mock(HouseholdRepository.class);
        FootprintService footprintService = mock(FootprintService.class);
        Household largeHousehold = household(1L, "Large baseline", "North");
        Household smallHousehold = household(2L, "Small baseline", "South");
        Household zeroBaselineHousehold = household(3L, "Zero baseline", "West");
        when(householdRepository.findAll()).thenReturn(
                List.of(largeHousehold, smallHousehold, zeroBaselineHousehold));

        YearMonth currentMonth = YearMonth.of(2026, 9);
        YearMonth previousMonth = currentMonth.minusMonths(1);
        when(footprintService.calculateMonthlyFootprint(1L, previousMonth)).thenReturn(1000.0);
        when(footprintService.calculateMonthlyFootprint(1L, currentMonth)).thenReturn(800.0);
        when(footprintService.calculateMonthlyFootprint(2L, previousMonth)).thenReturn(100.0);
        when(footprintService.calculateMonthlyFootprint(2L, currentMonth)).thenReturn(50.0);
        when(footprintService.calculateMonthlyFootprint(3L, previousMonth)).thenReturn(0.0);
        when(footprintService.calculateMonthlyFootprint(3L, currentMonth)).thenReturn(25.0);

        List<LeaderboardEntry> results = new LeaderboardService(householdRepository, footprintService)
                .getLeaderboard(currentMonth);

        assertEquals(List.of(2L, 1L, 3L), results.stream().map(LeaderboardEntry::householdId).toList());
        assertEquals(50.0, results.get(0).percentReduction());
        assertEquals(20.0, results.get(1).percentReduction());
        assertEquals(0.0, results.get(2).percentReduction());
    }

    private Household household(Long id, String name, String city) {
        Household household = new Household();
        household.setId(id);
        household.setName(name);
        household.setCity(city);
        return household;
    }
}