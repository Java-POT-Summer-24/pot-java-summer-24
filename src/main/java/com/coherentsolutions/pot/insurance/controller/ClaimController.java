package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.specifications.FilterAndSortCriteria;
import com.coherentsolutions.pot.insurance.specifications.FilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.SortCriteria;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
      @ParameterObject FilterAndSortCriteria criteria,
      @ParameterObject Pageable pageable) {

    FilterCriteria filterCriteria = Optional.ofNullable(criteria.getFilterCriteria())
        .orElse(new FilterCriteria());
    SortCriteria sortCriteria = Optional.ofNullable(criteria.getSortCriteria())
        .orElseGet(() -> {
          SortCriteria defaultSort = new SortCriteria();
          defaultSort.setField("dateOfService");
          defaultSort.setDirection(Sort.Direction.DESC);
          return defaultSort;
        });

    int pageSize = pageable.getPageSize() == 20 ? 10 : pageable.getPageSize();
    Sort sort = Sort.by(sortCriteria.getDirection(), sortCriteria.getField());
    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageSize, sort);

    Page<ClaimDTO> claimsPage = claimService.getFilteredSortedClaims(filterCriteria, sortCriteria, pageRequest.getPageNumber(), pageRequest.getPageSize());

    return ResponseEntity.ok(claimsPage);
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