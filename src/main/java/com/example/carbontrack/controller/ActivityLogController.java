package com.example.carbontrack.controller;

import com.example.carbontrack.dto.ActivityLogRequest;
import com.example.carbontrack.entity.ActivityLog;
import com.example.carbontrack.service.ActivityLogService;
import com.example.carbontrack.service.FootprintService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/households/{id}")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final FootprintService footprintService;

    public ActivityLogController(ActivityLogService activityLogService, FootprintService footprintService) {
        this.activityLogService = activityLogService;
        this.footprintService = footprintService;
    }

    @PostMapping("/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityLog createActivity(@PathVariable Long id, @Valid @RequestBody ActivityLogRequest request) {
        return activityLogService.createActivity(id, request);
    }

    @GetMapping("/activities")
    public List<ActivityLog> getActivities(@PathVariable Long id) {
        return activityLogService.getActivities(id);
    }

    @GetMapping("/footprint")
    public double getMonthlyFootprint(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return footprintService.calculateMonthlyFootprint(id, month);
    }

    @GetMapping("/footprint/history")
    public Map<YearMonth, Double> getFootprintHistory(@PathVariable Long id) {
        return footprintService.getFootprintHistory(id, 6);
    }
}