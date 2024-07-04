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
import com.coherentsolutions.pot.insurance.util.ClaimNumberGenerator;
import lombok.RequiredArgsConstructor;
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

    EmployeeEntity consumer = employeeRepository.findByUserName(claimDTO.getConsumer())
        .orElseThrow(() -> new NotFoundException("Employee with userName " + claimDTO.getConsumer() + " not found"));
    CompanyEntity employer = companyRepository.findByName(claimDTO.getEmployer())
        .orElseThrow(() -> new NotFoundException("Company with name " + claimDTO.getEmployer() + " not found"));

    ClaimEntity claim = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);
    claim.setConsumer(consumer);
    claim.setEmployer(employer);
    claim = claimRepository.save(claim);
    return ClaimMapper.INSTANCE.entityToDto(claim);
  }

  public ClaimDTO updateClaim(ClaimDTO claimDTO) {
    UUID claimId = claimDTO.getId();
    ClaimEntity existingClaim = claimRepository.findById(claimId)
        .orElseThrow(() -> new NotFoundException("Claim with ID " + claimId + " not found"));

    EmployeeEntity consumer = employeeRepository.findByUserName(claimDTO.getConsumer())
        .orElseThrow(() -> new NotFoundException("Employee with userName " + claimDTO.getConsumer() + " not found"));
    CompanyEntity employer = companyRepository.findByName(claimDTO.getEmployer())
        .orElseThrow(() -> new NotFoundException("Company with name " + claimDTO.getEmployer() + " not found"));

    ClaimMapper.INSTANCE.updateClaimFromDTO(claimDTO, existingClaim);
    existingClaim.setConsumer(consumer);
    existingClaim.setEmployer(employer);
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

  public List<ClaimDTO> getClaimsByConsumer(String consumer) {
    List<ClaimEntity> claims = claimRepository.findByConsumer_UserName(consumer);
    return claims.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());
  }

  public List<ClaimDTO> getClaimsByEmployer(String employer) {
    List<ClaimEntity> claims = claimRepository.findByEmployer_Name(employer);
    return claims.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());
  }
}