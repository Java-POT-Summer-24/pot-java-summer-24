package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.exception.BadRequestException;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.ClaimMapper;
import com.coherentsolutions.pot.insurance.repository.ClaimRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimService {

  private final ClaimRepository claimRepository;

  public List<ClaimDTO> getAllClaims() {
    return claimRepository.findAll().stream()
        .map(ClaimMapper.INSTANCE::claimToClaimDTO)
        .collect(Collectors.toList());
  }

  public ClaimDTO getClaimById(UUID id) {
    ClaimEntity claim = claimRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Claim with ID " + id + " not found"));
    return ClaimMapper.INSTANCE.claimToClaimDTO(claim);
  }

  public ClaimDTO addClaim(ClaimDTO claimDTO) {
    if (claimRepository.existsByClaimNumber(claimDTO.getClaimNumber())) {
      throw new BadRequestException("Claim with number " + claimDTO.getClaimNumber() + " already exists.");
    }
    ClaimEntity claim = ClaimMapper.INSTANCE.claimDTOToClaim(claimDTO);
    if (claim.getId() == null) {
      claim.setId(UUID.randomUUID());
    }
    claim = claimRepository.save(claim);
    return ClaimMapper.INSTANCE.claimToClaimDTO(claim);
  }

  public ClaimDTO updateClaim(ClaimDTO claimDTO) {
    if (!claimRepository.existsById(claimDTO.getId())) {
      throw new NotFoundException("Claim with ID " + claimDTO.getId() + " not found");
    }

    ClaimEntity existingClaim = claimRepository.findByClaimNumber(claimDTO.getClaimNumber());
    if (existingClaim != null && !existingClaim.getId().equals(claimDTO.getId())) {
      throw new BadRequestException("Claim with number " + claimDTO.getClaimNumber() + " already exists.");
    }

    ClaimEntity claim = ClaimMapper.INSTANCE.claimDTOToClaim(claimDTO);
    claim = claimRepository.save(claim);
    return ClaimMapper.INSTANCE.claimToClaimDTO(claim);
  }

  public void deleteClaim(UUID id) {
    if (!claimRepository.existsById(id)) {
      throw new NotFoundException("Claim with ID " + id + " not found");
    }
    claimRepository.deleteById(id);
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
}
