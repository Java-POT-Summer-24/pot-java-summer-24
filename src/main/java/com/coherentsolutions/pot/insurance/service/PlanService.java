package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.dto.enums.PlanStatus;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.exception.BadRequestException;
import com.coherentsolutions.pot.insurance.mapper.PlanMapper;
import com.coherentsolutions.pot.insurance.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    public PlanDTO addPlan(PlanDTO planDTO) {
            UUID planId = planDTO.getPlanId();
            if(planRepository.existsByPlanId(planId)){
                throw new BadRequestException("Plan with ID " + planId + " already exists");
            }
            PlanEntity plan = PlanMapper.INSTANCE.toPlanEntity(planDTO);

            plan = planRepository.save(plan);
            return PlanMapper.INSTANCE.toPlanDto(plan);
    }
    public List<PlanDTO> getAllPlans() {
            return planRepository.findAll().stream()
                    .map(PlanMapper.INSTANCE::toPlanDto)
                    .collect(Collectors.toList());
    }

    public PlanDTO updatePlan(PlanDTO planDTO){
            UUID planId = planDTO.getPlanId();
            PlanEntity existingPlan = planRepository.findByPlanId(planId)
                    .orElseThrow(() -> new NotFoundException("Plan with ID " + planId + " not found"));

            PlanMapper.INSTANCE.updatePlanFromDTO(planDTO, existingPlan);
            existingPlan = planRepository.save(existingPlan);

            return PlanMapper.INSTANCE.toPlanDto(existingPlan);
    }


    public PlanDTO deactivatePlan(UUID id){
        return planRepository.findByPlanId(id)
                .map(plan -> {
                    plan.setStatus(PlanStatus.DEACTIVATED);
                    plan = planRepository.save(plan);
                    return PlanMapper.INSTANCE.toPlanDto(plan);
                })
                .orElseThrow(() -> new NotFoundException("Plan with id " + id + " not found"));
    }
    public PlanDTO getPlanById(UUID id){
            PlanEntity plan = planRepository.findByPlanId(id)
                    .orElseThrow(() -> new NotFoundException("Plan with id " + id + " not found"));
            return PlanMapper.INSTANCE.toPlanDto(plan);
    }
}
