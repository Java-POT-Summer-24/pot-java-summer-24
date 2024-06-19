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
    List<ClaimDTO> claims = claimService.getAllClaims();
    return ResponseEntity.ok(claims);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClaimDTO> getClaimById(@PathVariable UUID id) {
    ClaimDTO claim = claimService.getClaimById(id);
    return ResponseEntity.ok(claim);
  }

  @PostMapping
  public ResponseEntity<ClaimDTO> addClaim(@RequestBody ClaimDTO claimDTO) {
    ClaimDTO newClaim = claimService.addClaim(claimDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(newClaim);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClaimDTO> updateClaim(@PathVariable UUID id, @Valid @RequestBody ClaimDTO claimDTO) {
    claimDTO.setId(id);
    ClaimDTO updatedClaim = claimService.updateClaim(claimDTO);
    return ResponseEntity.ok(updatedClaim);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteClaim(@PathVariable UUID id) {
    claimService.deleteClaim(id);
    return ResponseEntity.noContent().build();
  }

  /*@GetMapping("/company/{companyName}")
  public ResponseEntity<List<ClaimDTO>> getClaimsByCompany(@PathVariable String name) {
    List<ClaimDTO> claims = claimService.getClaimsByCompany(name);
    return ResponseEntity.ok(claims);
  }

  @GetMapping("/user/{userName}")
  public ResponseEntity<List<ClaimDTO>> getClaimsByUser(@PathVariable String userName) {
    List<ClaimDTO> claims = claimService.getClaimsByUser(userName);
    return ResponseEntity.ok(claims);
  }*/
  //also commented for now, as I don't have employer or consumer
}
