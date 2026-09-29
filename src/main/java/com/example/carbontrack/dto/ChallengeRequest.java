package com.example.carbontrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ChallengeRequest(
        @NotBlank String title,
        String description,
        @NotNull @Positive Double targetReductionPercent,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate) {
}