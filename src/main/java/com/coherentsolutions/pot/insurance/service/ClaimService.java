package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.ClaimMapper;
import com.coherentsolutions.pot.insurance.repository.ClaimRepository;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import com.coherentsolutions.pot.insurance.specifications.ClaimFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.ClaimSpecifications;
import com.coherentsolutions.pot.insurance.util.ClaimNumberGenerator;
import com.coherentsolutions.pot.insurance.util.NotificationClient;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClaimService {

  private final ClaimRepository claimRepository;
  private final EmployeeRepository employeeRepository;
  private final CompanyRepository companyRepository;
  private final NotificationClient notificationClient;

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
    return claimRepository.findById(id)
        .map(ClaimMapper.INSTANCE::entityToDto)
        .orElseThrow(() -> new NotFoundException("Claim with ID " + id + " not found"));
  }

  public ClaimDTO addClaim(ClaimDTO claimDTO) {
    String generatedClaimNumber = ClaimNumberGenerator.generate();
    claimDTO.setClaimNumber(generatedClaimNumber);

    // in ClaimDTO we get username as getEmployee
    String employeeUserName = claimDTO.getEmployeeUserName();
    EmployeeEntity employee = employeeRepository.findByUserName(employeeUserName).orElseThrow(
        () -> new NotFoundException(
            "Employee with userName " + employeeUserName + " not found"));
    CompanyEntity company = companyRepository.findByName(claimDTO.getCompany()).orElseThrow(
        () -> new NotFoundException("Company with name " + claimDTO.getCompany() + " not found"));

    ClaimEntity claim = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);
    claim.setEmployee(employee);
    claim.setCompany(company);
    claim = claimRepository.save(claim);
    return ClaimMapper.INSTANCE.entityToDto(claim);
  }

  public ClaimDTO updateClaim(UUID claimId, ClaimDTO claimDTO) {
    return claimRepository.findById(claimId)
        .map(existingClaim -> {
          if (existingClaim.getStatus() == ClaimStatus.DEACTIVATED) {
            throw new NotFoundException("Cannot update. Claim with ID " + claimId + " is deactivated");
          }
          ClaimMapper.INSTANCE.updateClaimFromDTO(claimDTO, existingClaim);
          return ClaimMapper.INSTANCE.entityToDto(claimRepository.save(existingClaim));
        }).orElseThrow(() -> new NotFoundException("Claim not found with id: " + claimId));
  }

  public ClaimDTO deactivateClaim(UUID id) {
    return claimRepository.findById(id).map(claim -> {
      claim.setStatus(ClaimStatus.DEACTIVATED);
      claim = claimRepository.save(claim);

      // Send notification
      String message = """
          Dear %s,

          The claim with number %s has been deactivated.

          Best regards,
          Your Company
          """.formatted(claim.getEmployee().getFirstName(), claim.getClaimNumber());

      notificationClient.sendDeactivationNotification(claim.getEmployee().getEmail(),
          "Claim Deactivated", message);

      return ClaimMapper.INSTANCE.entityToDto(claim);
    }).orElseThrow(() -> new NotFoundException("Claim with ID " + id + " was not found"));
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
