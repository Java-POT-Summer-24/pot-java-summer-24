package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.service.PlanService;
import com.coherentsolutions.pot.insurance.specifications.PlanFilterCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/plans")
@RequiredArgsConstructor
public class PlanController {

  private final PlanService planService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canGetAllPlans(authentication)")
  @Cacheable(value = "plansList")
  public List<PlanDTO> getAllPlans() {
    return planService.getAllPlans();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canAccessPlan(authentication, #id)")
  @Cacheable(value = "plan", key = "#id")
  public PlanDTO getPlanById(@PathVariable UUID id) {
    return planService.getPlanById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.canAddPlan(authentication)")
  @CacheEvict(value = "plansList", allEntries = true)
  public PlanDTO addPlan(@Valid @RequestBody PlanDTO planDTO) {
    return planService.addPlan(planDTO);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canUpdatePlan(authentication, #id)")
  @Caching(
      evict = {@CacheEvict(value = "plansList", allEntries = true)},
      put = {@CachePut(value = "plan", key = "#id")}
  )
  public PlanDTO updatePlan(@PathVariable UUID id, @Valid @RequestBody PlanDTO planDTO) {
    return planService.updatePlan(id, planDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canDeletePlan(authentication, #id)")
  @Caching(
      evict = {@CacheEvict(value = "plansList", allEntries = true)},
      put = {@CachePut(value = "plan", key = "#id")}
  )
  public PlanDTO deactivatePlan(@PathVariable UUID id) {
    return planService.deactivatePlan(id);
  }

  @GetMapping("/filtered")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canGetFilteredSortedPlans(authentication)")
  public Page<PlanDTO> getFilteredSortedPlans(
      @ParameterObject PlanFilterCriteria criteria,
      @ParameterObject Pageable pageable) {
    return planService.getFilteredSortedPlans(criteria, pageable);
  }
}
