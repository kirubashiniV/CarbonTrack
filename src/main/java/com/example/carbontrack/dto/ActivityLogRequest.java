package com.example.carbontrack.dto;

import com.example.carbontrack.entity.ActivityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ActivityLogRequest(
        @NotNull ActivityType activityType,
        @NotNull @Positive Double value,
        @NotNull LocalDate logDate) {
}