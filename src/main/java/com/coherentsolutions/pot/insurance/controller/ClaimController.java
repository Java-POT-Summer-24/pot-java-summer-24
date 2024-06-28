package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

  private final ClaimService claimService;

  @GetMapping
  public ResponseEntity<List<ClaimDTO>> getAllClaims() {
    return ResponseEntity.ok(claimService.getAllClaims());
  }

  @GetMapping("/filtered")
  public ResponseEntity<Page<ClaimDTO>> getFilteredSortedClaims(
      @RequestParam(required = false) String claimNumber,
      @RequestParam(required = false) String consumer,
      @RequestParam(required = false) String employer,
      @RequestParam(required = false) ClaimStatus status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "dateOfService,desc") String sort) {
    String[] sortParams = sort.split(",");
    Sort.Direction dir = "asc".equalsIgnoreCase(sortParams[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sorting = Sort.by(dir, sortParams[0]);

    return ResponseEntity.ok(claimService.getFilteredSortedClaims(
        claimNumber, consumer, employer, status, page, size, sorting));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClaimDTO> getClaimById(@PathVariable UUID id) {
    return ResponseEntity.ok(claimService.getClaimById(id));
  }

  @PostMapping
  public ResponseEntity<ClaimDTO> addClaim(@Valid @RequestBody ClaimDTO claimDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(claimService.addClaim(claimDTO));
  }

  @PutMapping
  public ResponseEntity<ClaimDTO> updateClaim(@Valid @RequestBody ClaimDTO claimDTO) {
    return ResponseEntity.ok(claimService.updateClaim(claimDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ClaimDTO> deactivateClaim(@PathVariable UUID id) {
    return ResponseEntity.ok(claimService.deactivateClaim(id));
  }

  /*@GetMapping("/company/{companyName}")
  public ResponseEntity<List<ClaimDTO>> getClaimsByCompany(@PathVariable String name) {
    return ResponseEntity.ok(claimService.getClaimsByCompany(name));
  }

  @GetMapping("/user/{userName}")
  public ResponseEntity<List<ClaimDTO>> getClaimsByUser(@PathVariable String userName) {
    return ResponseEntity.ok(claimService.getClaimsByUser(userName));
  }*/
  //also commented for now, as I don't have employer or consumer
}