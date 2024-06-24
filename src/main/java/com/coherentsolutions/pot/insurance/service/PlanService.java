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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    public ResponseEntity<PlanDTO> addPlan(PlanDTO planDTO) {
        try{
            UUID planId = planDTO.getPlanId();
            if(planRepository.existsByPlanId(planId)){
                throw new BadRequestException("Plan with ID " + planId + " already exists");
            }
            PlanEntity plan = PlanMapper.INSTANCE.toPlanEntity(planDTO);

            plan = planRepository.save(plan);
            return new ResponseEntity<>(PlanMapper.INSTANCE.toPlanDto(plan), HttpStatus.CREATED);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
    }
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        try{
            return new ResponseEntity<>(planRepository.findAll().stream()
                    .map(PlanMapper.INSTANCE::toPlanDto)
                    .collect(Collectors.toList()), HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<PlanDTO> updatePlan(PlanDTO planDTO){
        try{
            UUID planId = planDTO.getPlanId();
            PlanEntity existingPlan = planRepository.findByPlanId(planId)
                    .orElseThrow(() -> new NotFoundException("Plan with ID " + planId + " not found"));

            PlanMapper.INSTANCE.updatePlanFromDTO(planDTO, existingPlan);
            existingPlan = planRepository.save(existingPlan);

            return new ResponseEntity<>(PlanMapper.INSTANCE.toPlanDto(existingPlan), HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<PlanDTO> deactivatePlan(UUID id){
        return planRepository.findByPlanId(id)
                .map(plan -> {
                    plan.setStatus(PlanStatus.DEACTIVATED);
                    plan = planRepository.save(plan);
                    return new ResponseEntity<>(PlanMapper.INSTANCE.toPlanDto(plan), HttpStatus.OK);
                })
                .orElseThrow(() -> new NotFoundException("Plan with id " + id + " not found"));
    }
    public ResponseEntity<PlanDTO> getPlanById(UUID id){
        try{
            PlanEntity plan = planRepository.findByPlanId(id)
                    .orElseThrow(() -> new NotFoundException("Plan with id " + id + " not found"));
            return new ResponseEntity<>(PlanMapper.INSTANCE.toPlanDto(plan), HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
