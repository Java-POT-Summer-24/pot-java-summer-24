package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.specifications.FilterAndSortCriteria;
import com.coherentsolutions.pot.insurance.specifications.FilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.SortCriteria;
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

  @PostMapping("/filtered")
  public ResponseEntity<Page<ClaimDTO>> getFilteredSortedClaims(
      @RequestBody FilterAndSortCriteria criteria,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    FilterCriteria filterCriteria = criteria.getFilterCriteria();
    SortCriteria sortCriteria = criteria.getSortCriteria();

    // default sorting if no sort criteria are provided
    if (sortCriteria == null) {
      sortCriteria = new SortCriteria();
      sortCriteria.setField("dateOfService");
      sortCriteria.setDirection(Sort.Direction.DESC);
    }

    return ResponseEntity.ok(claimService.getFilteredSortedClaims(filterCriteria, sortCriteria, page, size));
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