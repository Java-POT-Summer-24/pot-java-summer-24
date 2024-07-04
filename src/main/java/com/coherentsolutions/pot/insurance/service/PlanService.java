package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.PlanMapper;
import com.coherentsolutions.pot.insurance.repository.PlanRepository;
import com.coherentsolutions.pot.insurance.specifications.PlanFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.PlanSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

  private final PlanRepository planRepository;

  public PlanDTO addPlan(PlanDTO planDTO) {
    PlanEntity plan = PlanMapper.INSTANCE.toPlanEntity(planDTO);
    PlanEntity createdPlan = planRepository.save(plan);

    return PlanMapper.INSTANCE.toPlanDto(createdPlan);
  }

  public List<PlanDTO> getAllPlans() {
    return planRepository.findAll().stream()
        .map(PlanMapper.INSTANCE::toPlanDto)
        .collect(Collectors.toList());
  }

  public PlanDTO updatePlan(UUID planId, PlanDTO planDTO) {
    PlanEntity existingPlan = planRepository.findById(planId)
        .orElseThrow(() -> new NotFoundException("Plan with ID " + planId + " not found"));

    PlanMapper.INSTANCE.updatePlanFromDTO(planDTO, existingPlan);
    existingPlan = planRepository.save(existingPlan);

    return PlanMapper.INSTANCE.toPlanDto(existingPlan);
  }

  public PlanDTO deactivatePlan(UUID id) {
    return planRepository.findById(id)
        .map(plan -> {
          plan.setStatus(PlanStatus.DEACTIVATED);
          plan = planRepository.save(plan);
          return PlanMapper.INSTANCE.toPlanDto(plan);
        })
        .orElseThrow(() -> new NotFoundException("Plan with id " + id + " not found"));
  }

  public PlanDTO getPlanById(UUID id) {
    PlanEntity plan = planRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Plan with id " + id + " not found"));
    return PlanMapper.INSTANCE.toPlanDto(plan);
  }

  public Page<PlanDTO> getFilteredSortedPlans(PlanFilterCriteria criteria,
      Pageable pageable) {
    Sort defaultSort = Sort.by("planName").ascending();

    if (!pageable.getSort().isSorted()) {
      pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
    }

    Specification<PlanEntity> spec = buildSpecification(criteria);
    return planRepository.findAll(spec, pageable).map(PlanMapper.INSTANCE::toPlanDto);
  }

  private Specification<PlanEntity> buildSpecification(PlanFilterCriteria planFilterCriteria) {
    Specification<PlanEntity> spec = Specification.where(null);

    if (isNotEmpty(planFilterCriteria.getPlanName())) {
      spec = spec.and(PlanSpecifications.byName(planFilterCriteria.getPlanName()));
    }

    if (planFilterCriteria.getPlanType() != null) {
      spec = spec.and(PlanSpecifications.byType(planFilterCriteria.getPlanType()));
    }

    if (planFilterCriteria.getStatus() != null) {
      spec = spec.and(PlanSpecifications.byStatus(planFilterCriteria.getStatus()));
    }

    return spec;
  }

  private boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }

}
