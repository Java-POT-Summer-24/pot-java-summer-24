package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.ClaimMapper;
import com.coherentsolutions.pot.insurance.repository.ClaimRepository;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import com.coherentsolutions.pot.insurance.repository.PlanRepository;
import com.coherentsolutions.pot.insurance.specifications.ClaimFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.ClaimSpecifications;
import com.coherentsolutions.pot.insurance.util.ClaimNumberGenerator;
import jakarta.transaction.Transactional;
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
  private final EmployeeRepository employeeRepository;
  private final CompanyRepository companyRepository;
  private final PlanRepository planRepository;

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

  @Transactional
  public ClaimDTO addClaim(ClaimDTO claimDTO) {
    String generatedClaimNumber = ClaimNumberGenerator.generate();
    claimDTO.setClaimNumber(generatedClaimNumber);

    EmployeeEntity employee = employeeRepository.findByUserName(claimDTO.getEmployee())
        .orElseThrow(() -> new NotFoundException("Employee with userName " + claimDTO.getEmployee() + " not found"));
    CompanyEntity company = companyRepository.findByName(claimDTO.getCompany())
        .orElseThrow(() -> new NotFoundException("Company with name " + claimDTO.getCompany() + " not found"));
    PlanEntity plan = planRepository.findById(claimDTO.getPlanId())
        .orElseThrow(() -> new NotFoundException("Plan with ID " + claimDTO.getPlanId() + " not found"));

    if (claimDTO.getStatus() == ClaimStatus.APPROVED) {
      double newRemainingLimit = plan.getRemainingLimit() - claimDTO.getAmount();
      if (newRemainingLimit < 0) {
        throw new IllegalStateException("Claim amount exceeds plan's remaining limit");
      }
      plan.setRemainingLimit(newRemainingLimit);
      planRepository.save(plan);
    }

    ClaimEntity claim = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);
    claim.setEmployee(employee);
    claim.setCompany(company);
    claim.setPlanId(plan);
    claim = claimRepository.save(claim);
    return ClaimMapper.INSTANCE.entityToDto(claim);
  }

  @Transactional
  public ClaimDTO updateClaim(UUID claimId, ClaimDTO claimDTO) {
    ClaimEntity existingClaim = claimRepository.findById(claimId)
        .orElseThrow(() -> new NotFoundException("Claim with ID " + claimId + " not found"));

    if (claimDTO.getStatus() == ClaimStatus.APPROVED && existingClaim.getStatus() != ClaimStatus.APPROVED) {
      PlanEntity plan = planRepository.findById(claimDTO.getPlanId())
          .orElseThrow(() -> new NotFoundException("Plan with ID " + claimDTO.getPlanId() + " not found"));

      double newRemainingLimit = plan.getRemainingLimit() - claimDTO.getAmount();
      if (newRemainingLimit < 0) {
        throw new IllegalStateException("Claim amount exceeds plan's remaining limit");
      }

      plan.setRemainingLimit(newRemainingLimit);
      planRepository.save(plan);
    }

    ClaimMapper.INSTANCE.updateClaimFromDTO(claimDTO, existingClaim);
    existingClaim.setPlanId(planRepository.findById(claimDTO.getPlanId())
        .orElseThrow(() -> new NotFoundException("Plan with ID " + claimDTO.getPlanId() + " not found"))); // Ensure planId is correctly set
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

  public List<ClaimDTO> getAllClaimsByEmployeeUserName(String employeeUserName) {
    List<ClaimEntity> claims = claimRepository.findAllByEmployeeUserName(employeeUserName);
    return claims.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());
  }

  public List<ClaimDTO> getAllClaimsByCompanyName(String companyName) {
    List<ClaimEntity> claims = claimRepository.findAllByCompanyName(companyName);
    return claims.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());
  }

  private Specification<ClaimEntity> buildSpecification(ClaimFilterCriteria claimFilterCriteria) {
    Specification<ClaimEntity> spec = Specification.where(null);

    if (isNotEmpty(claimFilterCriteria.getClaimNumber())) {
      spec = spec.and(ClaimSpecifications.byClaimNumber(claimFilterCriteria.getClaimNumber()));
    }
    if (claimFilterCriteria.getEmployee() != null) {
      spec = spec.and(ClaimSpecifications.byEmployeeUserName(claimFilterCriteria.getEmployee()));
    }
    if (claimFilterCriteria.getCompany() != null) {
      spec = spec.and(ClaimSpecifications.byCompanyName(claimFilterCriteria.getCompany()));
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
