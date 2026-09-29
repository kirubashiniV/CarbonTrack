package com.example.carbontrack.dto;

import jakarta.validation.constraints.NotNull;

public record JoinChallengeRequest(@NotNull Long householdId) {
}