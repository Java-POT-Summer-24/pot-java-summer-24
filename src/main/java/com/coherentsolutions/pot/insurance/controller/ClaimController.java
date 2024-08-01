package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.specifications.ClaimFilterCriteria;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

  private final ClaimService claimService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Cacheable(value = "claimsList")
  @PreAuthorize("@securityService.canGetAllClaims(authentication)")
  public List<ClaimDTO> getAllClaims() {
    return claimService.getAllClaims();
  }

  @GetMapping("/filtered")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canAccessFilteredSortedClaims(authentication, #claimFilterCriteria)")
  public Page<ClaimDTO> getFilteredSortedClaims(
          @ParameterObject ClaimFilterCriteria claimFilterCriteria,
          @ParameterObject Pageable pageable) {
    return claimService.getFilteredSortedClaims(claimFilterCriteria, pageable);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Cacheable(value = "claim", key = "#id")
  @PreAuthorize("@securityService.canAccessClaim(authentication, #id)")
  public ClaimDTO getClaimById(@PathVariable UUID id) {
    return claimService.getClaimById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Caching(evict = {
      @CacheEvict(value = {"claimsList", "plansList"}, allEntries = true),
      @CacheEvict(value = "plan", key = "#result.planId", allEntries = true)
  })
  @PreAuthorize("@securityService.canAddClaim(authentication, #claimDTO)")
  public ClaimDTO addClaim(@Valid @RequestBody ClaimDTO claimDTO) {
    return claimService.addClaim(claimDTO);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Caching(
      evict = {@CacheEvict(value = "claimsList", allEntries = true),
          @CacheEvict(value = "plansList", allEntries = true),
          @CacheEvict(value = "plan", key = "#claimDTO.planId", allEntries = true)},
      put = {@CachePut(value = "claim", key = "#id")}
  )
  @PreAuthorize("@securityService.canUpdateClaim(authentication, #id)")
  public ClaimDTO updateClaim(@PathVariable UUID id, @Valid @RequestBody ClaimDTO claimDTO) {
    return claimService.updateClaim(id, claimDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Caching(
          evict = {@CacheEvict(value = "claimsList", allEntries = true)},
          put = {@CachePut(value = "claim", key = "#id")}
  )
  @PreAuthorize("@securityService.canDeactivateClaim(authentication, #id)")
  public ClaimDTO deactivateClaim(@PathVariable UUID id) {
    return claimService.deactivateClaim(id);
  }

  @GetMapping("/employees/{employeeUserName}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canAccessClaimsByEmployee(authentication, #employeeUserName)")
  public List<ClaimDTO> getAllClaimsByEmployeeUserName(@PathVariable String employeeUserName) {
    return claimService.getAllClaimsByEmployeeUserName(employeeUserName);
  }

  @GetMapping("/companies/{companyName}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canAccessClaimsByCompany(authentication, #companyName)")
  public List<ClaimDTO> getAllClaimsByCompanyName(@PathVariable String companyName) {
    return claimService.getAllClaimsByCompanyName(companyName);
  }
}