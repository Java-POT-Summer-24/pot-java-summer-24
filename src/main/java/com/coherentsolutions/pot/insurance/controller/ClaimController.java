package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

  @GetMapping("/consumer")
  public ResponseEntity<List<ClaimDTO>> getClaimsByConsumer(@RequestParam String consumer) {
    return ResponseEntity.ok(claimService.getClaimsByConsumer(consumer));
  }

  @GetMapping("/employer")
  public ResponseEntity<List<ClaimDTO>> getClaimsByEmployer(@RequestParam String employer) {
    return ResponseEntity.ok(claimService.getClaimsByEmployer(employer));
  }

}