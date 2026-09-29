package com.example.carbontrack.controller;

import com.example.carbontrack.dto.LeaderboardEntry;
import com.example.carbontrack.service.LeaderboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public List<LeaderboardEntry> getLeaderboard(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return leaderboardService.getLeaderboard(month);
    }
}