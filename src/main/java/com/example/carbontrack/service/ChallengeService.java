package com.example.carbontrack.service;

import com.example.carbontrack.dto.ChallengeRequest;
import com.example.carbontrack.entity.Challenge;
import com.example.carbontrack.entity.Household;
import com.example.carbontrack.entity.Participation;
import com.example.carbontrack.exception.ResourceNotFoundException;
import com.example.carbontrack.repository.ChallengeRepository;
import com.example.carbontrack.repository.HouseholdRepository;
import com.example.carbontrack.repository.ParticipationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final HouseholdRepository householdRepository;
    private final ParticipationRepository participationRepository;
    private final FootprintService footprintService;

    public ChallengeService(ChallengeRepository challengeRepository,
                            HouseholdRepository householdRepository,
                            ParticipationRepository participationRepository,
                            FootprintService footprintService) {
        this.challengeRepository = challengeRepository;
        this.householdRepository = householdRepository;
        this.participationRepository = participationRepository;
        this.footprintService = footprintService;
    }

    @Transactional
    public Challenge createChallenge(ChallengeRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("Challenge endDate must be on or after startDate");
        }

        Challenge challenge = new Challenge();
        challenge.setTitle(request.title().trim());
        challenge.setDescription(request.description());
        challenge.setTargetReductionPercent(request.targetReductionPercent());
        challenge.setStartDate(request.startDate());
        challenge.setEndDate(request.endDate());
        return challengeRepository.save(challenge);
    }

    @Transactional
    public List<Challenge> getChallenges() {
        return challengeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Challenge getChallenge(Long challengeId) {
        return findChallenge(challengeId);
    }

    @Transactional
    public void deleteChallenge(Long challengeId) {
        Challenge challenge = findChallenge(challengeId);
        participationRepository.deleteByChallenge_Id(challenge.getId());
        challengeRepository.delete(challenge);
    }

    @Transactional
    public Participation joinChallenge(Long challengeId, Long householdId) {
        Challenge challenge = findChallenge(challengeId);
        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new ResourceNotFoundException("Household " + householdId + " was not found"));

        if (participationRepository.existsByHousehold_IdAndChallenge_Id(householdId, challengeId)) {
            throw new IllegalStateException("Household has already joined this challenge");
        }
        if (challenge.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("This challenge has already ended");
        }

        Participation participation = new Participation();
        participation.setHousehold(household);
        participation.setChallenge(challenge);
        participation.setJoinedAt(LocalDate.now());
        updateProgress(participation);
        return participationRepository.save(participation);
    }

    @Transactional
    public List<Participation> getParticipants(Long challengeId) {
        Challenge challenge = findChallenge(challengeId);
        List<Participation> participants = participationRepository
                .findByChallenge_IdOrderByJoinedAtAsc(challengeId);
        participants.forEach(participation -> updateProgress(participation, challenge));
        return participationRepository.saveAll(participants);
    }

    private void updateProgress(Participation participation) {
        updateProgress(participation, participation.getChallenge());
    }

    private void updateProgress(Participation participation, Challenge challenge) {
        YearMonth baselineMonth = YearMonth.from(challenge.getStartDate()).minusMonths(1);
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = YearMonth.from(challenge.getStartDate());
        YearMonth endMonth = YearMonth.from(challenge.getEndDate());

        if (currentMonth.isBefore(startMonth)) {
            currentMonth = startMonth;
        } else if (currentMonth.isAfter(endMonth)) {
            currentMonth = endMonth;
        }

        double baseline = footprintService.calculateMonthlyFootprint(
                participation.getHousehold().getId(), baselineMonth);
        double current = footprintService.calculateMonthlyFootprint(
                participation.getHousehold().getId(), currentMonth);
        // Progress is reduction against the month before the challenge; a zero baseline has no reduction.
        double progress = baseline == 0.0 ? 0.0 : ((baseline - current) / baseline) * 100.0;

        participation.setBaselineFootprint(baseline);
        participation.setCurrentFootprint(current);
        participation.setProgressPercent(progress);
    }

    private Challenge findChallenge(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge " + challengeId + " was not found"));
    }
}