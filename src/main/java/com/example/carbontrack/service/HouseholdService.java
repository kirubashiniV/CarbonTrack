package com.example.carbontrack.service;

import com.example.carbontrack.dto.HouseholdRequest;
import com.example.carbontrack.entity.Household;
import com.example.carbontrack.exception.ResourceNotFoundException;
import com.example.carbontrack.repository.ActivityLogRepository;
import com.example.carbontrack.repository.HouseholdRepository;
import com.example.carbontrack.repository.ParticipationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ParticipationRepository participationRepository;

    public HouseholdService(HouseholdRepository householdRepository,
                            ActivityLogRepository activityLogRepository,
                            ParticipationRepository participationRepository) {
        this.householdRepository = householdRepository;
        this.activityLogRepository = activityLogRepository;
        this.participationRepository = participationRepository;
    }

    @Transactional
    public Household createHousehold(HouseholdRequest request) {
        if (householdRepository.existsByEmail(request.email().trim())) {
            throw new IllegalStateException("A household with this email already exists");
        }

        Household household = new Household();
        household.setName(request.name().trim());
        household.setEmail(request.email().trim());
        household.setCity(request.city());
        return householdRepository.save(household);
    }

    @Transactional(readOnly = true)
    public List<Household> getHouseholds() {
        return householdRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Household getHousehold(Long householdId) {
        return findHousehold(householdId);
    }

    @Transactional
    public void deleteHousehold(Long householdId) {
        Household household = findHousehold(householdId);
        activityLogRepository.deleteByHousehold_Id(household.getId());
        participationRepository.deleteByHousehold_Id(household.getId());
        householdRepository.delete(household);
    }

    private Household findHousehold(Long householdId) {
        return householdRepository.findById(householdId)
                .orElseThrow(() -> new ResourceNotFoundException("Household " + householdId + " was not found"));
    }
}