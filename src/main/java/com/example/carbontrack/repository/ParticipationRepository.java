package com.example.carbontrack.repository;

import com.example.carbontrack.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByHousehold_IdAndChallenge_Id(Long householdId, Long challengeId);

    List<Participation> findByChallenge_IdOrderByJoinedAtAsc(Long challengeId);

    void deleteByHousehold_Id(Long householdId);

    void deleteByChallenge_Id(Long challengeId);
}