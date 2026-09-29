package com.example.carbontrack.dto;

public record LeaderboardEntry(
        Long householdId,
        String householdName,
        String city,
        double previousMonthFootprint,
        double currentMonthFootprint,
        double percentReduction) {
}