package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.specifications.FilterAndSortCriteria;
import com.coherentsolutions.pot.insurance.specifications.FilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.SortCriteria;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
  public List<ClaimDTO> getAllClaims() {
    return claimService.getAllClaims();
  }

  @GetMapping("/filtered")
  @ResponseStatus(HttpStatus.OK)
  public Page<ClaimDTO> getFilteredSortedClaims(
      @ParameterObject FilterAndSortCriteria criteria,
      @ParameterObject Pageable pageable) {

    FilterCriteria filterCriteria;
    SortCriteria sortCriteria;

    if (criteria == null || criteria.getFilterCriteria() == null) {
      filterCriteria = new FilterCriteria();
    } else {
      filterCriteria = criteria.getFilterCriteria();
    }

    if (criteria == null || criteria.getSortCriteria() == null) {
      sortCriteria = new SortCriteria();
      sortCriteria.setField("dateOfService");
      sortCriteria.setDirection(Sort.Direction.DESC);
    } else {
      sortCriteria = criteria.getSortCriteria();
    }

    return claimService.getFilteredSortedClaims(filterCriteria, sortCriteria, pageable.getPageNumber(), pageable.getPageSize());
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ClaimDTO getClaimById(@PathVariable UUID id) {
    return claimService.getClaimById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClaimDTO addClaim(@Valid @RequestBody ClaimDTO claimDTO) {
    return claimService.addClaim(claimDTO);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public ClaimDTO updateClaim(@Valid @RequestBody ClaimDTO claimDTO) {
    return claimService.updateClaim(claimDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ClaimDTO deactivateClaim(@PathVariable UUID id) {
    return claimService.deactivateClaim(id);
  }

  /*@GetMapping("/company/{companyName}")
  @ResponseStatus(HttpStatus.OK)
  public List<ClaimDTO> getClaimsByCompany(@PathVariable String name) {
    return claimService.getClaimsByCompany(name);
  }

  @GetMapping("/user/{userName}")
  @ResponseStatus(HttpStatus.OK)
  public List<ClaimDTO> getClaimsByUser(@PathVariable String userName) {
    return claimService.getClaimsByUser(userName);
  }*/
  //also commented for now, as I don't have employer or consumer
}