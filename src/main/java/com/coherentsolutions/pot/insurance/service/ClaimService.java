package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.ClaimMapper;
import com.coherentsolutions.pot.insurance.repository.ClaimRepository;
import com.coherentsolutions.pot.insurance.specifications.ClaimSpecifications;
import com.coherentsolutions.pot.insurance.specifications.ClaimFilterCriteria;
import com.coherentsolutions.pot.insurance.util.ClaimNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimService {

  private final ClaimRepository claimRepository;

  public Page<ClaimDTO> getFilteredSortedClaims(ClaimFilterCriteria claimFilterCriteria,
      Pageable pageable) {

    Sort defaultSort = Sort.by("dateOfService").descending();

    if (!pageable.getSort().isSorted()) {
      pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
    }

    Specification<ClaimEntity> spec = buildSpecification(claimFilterCriteria);
    return claimRepository.findAll(spec, pageable).map(ClaimMapper.INSTANCE::entityToDto);
  }

  public List<ClaimDTO> getAllClaims() {
    return claimRepository.findAll().stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());
  }

  public ClaimDTO getClaimById(UUID id) {
    ClaimEntity claim = claimRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Claim with ID " + id + " not found"));
    return ClaimMapper.INSTANCE.entityToDto(claim);
  }

  public ClaimDTO addClaim(ClaimDTO claimDTO) {
    String generatedClaimNumber = ClaimNumberGenerator.generate();
    claimDTO.setClaimNumber(generatedClaimNumber);

    ClaimEntity claim = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);
    claim = claimRepository.save(claim);
    return ClaimMapper.INSTANCE.entityToDto(claim);
  }

  public ClaimDTO updateClaim(UUID claimId, ClaimDTO claimDTO) {
    ClaimEntity existingClaim = claimRepository.findById(claimId)
        .orElseThrow(() -> new NotFoundException("Claim with ID " + claimId + " not found"));

    ClaimMapper.INSTANCE.updateClaimFromDTO(claimDTO, existingClaim);
    existingClaim = claimRepository.save(existingClaim);

    return ClaimMapper.INSTANCE.entityToDto(existingClaim);
  }

  public ClaimDTO deactivateClaim(UUID id) {
    return claimRepository.findById(id)
        .map(claim -> {
          claim.setStatus(ClaimStatus.DEACTIVATED);
          claim = claimRepository.save(claim);
          return ClaimMapper.INSTANCE.entityToDto(claim);
        })
        .orElseThrow(() -> new NotFoundException("Claim with ID " + id + " was not found"));
  }

  private Specification<ClaimEntity> buildSpecification(ClaimFilterCriteria claimFilterCriteria) {
    Specification<ClaimEntity> spec = Specification.where(null);

    if (isNotEmpty(claimFilterCriteria.getClaimNumber())) {
      spec = spec.and(ClaimSpecifications.byClaimNumber(claimFilterCriteria.getClaimNumber()));
    }
    if (isNotEmpty(claimFilterCriteria.getConsumer())) {
      spec = spec.and(ClaimSpecifications.byConsumer(claimFilterCriteria.getConsumer()));
    }
    if (isNotEmpty(claimFilterCriteria.getEmployer())) {
      spec = spec.and(ClaimSpecifications.byEmployer(claimFilterCriteria.getEmployer()));
    }
    if (claimFilterCriteria.getStatus() != null) {
      spec = spec.and(ClaimSpecifications.byStatus(claimFilterCriteria.getStatus()));
    }

    return spec;
  }

  private boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }
}

  // Blueprint, will uncomment and modify when company and user implementations are available
    /*
    public List<ClaimDTO> getClaimsByCompany(String companyName) {
        return claimRepository.findByEmployer(companyName).stream()
            .map(ClaimMapper.INSTANCE::claimToClaimDTO)
            .collect(Collectors.toList());
    }

    public List<ClaimDTO> getClaimsByUser(String userName) {
        return claimRepository.findByConsumer(userName).stream()
            .map(ClaimMapper.INSTANCE::claimToClaimDTO)
            .collect(Collectors.toList());
    }
    */
