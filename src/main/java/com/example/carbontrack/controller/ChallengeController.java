package com.example.carbontrack.controller;

import com.example.carbontrack.dto.ChallengeRequest;
import com.example.carbontrack.dto.JoinChallengeRequest;
import com.example.carbontrack.entity.Challenge;
import com.example.carbontrack.entity.Participation;
import com.example.carbontrack.service.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Challenge create(@Valid @RequestBody ChallengeRequest request) {
        return challengeService.createChallenge(request);
    }

    @GetMapping
    public List<Challenge> getAll() {
        return challengeService.getChallenges();
    }

    @GetMapping("/{id}")
    public Challenge getOne(@PathVariable Long id) {
        return challengeService.getChallenge(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
    }

    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.CREATED)
    public Participation join(@PathVariable Long id, @Valid @RequestBody JoinChallengeRequest request) {
        return challengeService.joinChallenge(id, request.householdId());
    }

    @GetMapping("/{id}/participants")
    public List<Participation> getParticipants(@PathVariable Long id) {
        return challengeService.getParticipants(id);
    }
}