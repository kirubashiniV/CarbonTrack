package com.example.carbontrack.controller;

import com.example.carbontrack.dto.HouseholdRequest;
import com.example.carbontrack.entity.Household;
import com.example.carbontrack.service.HouseholdService;
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
@RequestMapping("/api/households")
public class HouseholdController {

    private final HouseholdService householdService;

    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Household create(@Valid @RequestBody HouseholdRequest request) {
        return householdService.createHousehold(request);
    }

    @GetMapping
    public List<Household> getAll() {
        return householdService.getHouseholds();
    }

    @GetMapping("/{id}")
    public Household getOne(@PathVariable Long id) {
        return householdService.getHousehold(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        householdService.deleteHousehold(id);
    }
}