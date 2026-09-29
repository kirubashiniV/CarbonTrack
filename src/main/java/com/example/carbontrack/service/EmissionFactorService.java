package com.example.carbontrack.service;

import com.example.carbontrack.entity.ActivityType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmissionFactorService {

    // Fixed kg CO2 factors per km, kWh, or kg as appropriate for each activity.
    private static final Map<ActivityType, Double> FACTORS = Map.of(
            ActivityType.COMMUTE_CAR, 0.192,
            ActivityType.COMMUTE_BUS, 0.105,
            ActivityType.COMMUTE_BIKE, 0.000,
            ActivityType.COMMUTE_TRAIN, 0.041,
            ActivityType.ELECTRICITY, 0.820,
            ActivityType.WASTE, 0.500);

    public double getFactor(ActivityType type) {
        Double factor = FACTORS.get(type);
        if (factor == null) {
            throw new IllegalArgumentException("No emission factor is configured for " + type);
        }
        return factor;
    }
}