package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @GetMapping("allPlans")
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        return planService.getAllPlans();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable UUID id){
        return planService.getPlanById(id);
    }

    @PostMapping("add")
    public ResponseEntity<PlanDTO> addPlan(@Valid @RequestBody PlanDTO planDTO){
        return planService.addPlan(planDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDTO> updatePlan(@PathVariable UUID id, @Valid @RequestBody PlanDTO planDTO){
        planDTO.setPlanId(id);
        return planService.updatePlan(planDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PlanDTO> deactivatePlan(@PathVariable UUID id) {
        return planService.deactivatePlan(id);
    }
}
