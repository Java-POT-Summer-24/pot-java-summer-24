package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

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

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ClaimDTO updateClaim(@PathVariable UUID id, @Valid @RequestBody ClaimDTO claimDTO) {
    return claimService.updateClaim(id, claimDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ClaimDTO deactivateClaim(@PathVariable UUID id) {
    return claimService.deactivateClaim(id);
  }

  @GetMapping("/employees/{employeeUserName}")
  @ResponseStatus(HttpStatus.OK)
  public List<ClaimDTO> getAllClaimsByEmployeeUserName(@PathVariable String employeeUserName) {
    return claimService.getAllClaimsByEmployeeUserName(employeeUserName);
  }

  @GetMapping("/companies/{companyName}")
  @ResponseStatus(HttpStatus.OK)
  public List<ClaimDTO> getAllClaimsByCompanyName(@PathVariable String companyName) {
    return claimService.getAllClaimsByCompanyName(companyName);
  }

}