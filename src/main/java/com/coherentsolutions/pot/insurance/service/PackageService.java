package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.exception.BadRequestException;
import com.coherentsolutions.pot.insurance.mapper.PackageMapper;
import com.coherentsolutions.pot.insurance.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageService {

  private final PackageRepository packageRepository;

  public List<PackageDTO> getAllPackages() {
    return packageRepository.findAll().stream()
        .map(PackageMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());
  }

  public PackageDTO getPackageById(UUID id) {
    PackageEntity packageEntity = packageRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Package with ID " + id + " not found"));
    return PackageMapper.INSTANCE.entityToDto(packageEntity);
  }

  public PackageDTO addPackage(PackageDTO packageDTO) {
    packageRepository.findByName(packageDTO.getName())
        .ifPresent(existingPackage -> {
          throw new BadRequestException("Package with name " + packageDTO.getName() + " already exists.");
        });
    PackageEntity packageEntity = PackageMapper.INSTANCE.dtoToEntity(packageDTO);
    packageEntity = packageRepository.save(packageEntity);
    return PackageMapper.INSTANCE.entityToDto(packageEntity);
  }

  public PackageDTO updatePackage(PackageDTO packageDTO) {
    UUID packageId = packageDTO.getId();
    PackageEntity existingPackage = packageRepository.findById(packageId)
        .orElseThrow(() -> new NotFoundException("Package with ID " + packageId + " not found"));
    PackageMapper.INSTANCE.updatePackageFromDTO(packageDTO, existingPackage);
    existingPackage = packageRepository.save(existingPackage);
    return PackageMapper.INSTANCE.entityToDto(existingPackage);
  }

  public PackageDTO deactivatePackage(UUID id) {
    return packageRepository.findById(id)
        .map(packageEntity -> {
          packageEntity.setStatus(PackageStatus.DEACTIVATED);
          packageEntity = packageRepository.save(packageEntity);
          return PackageMapper.INSTANCE.entityToDto(packageEntity);
        })
        .orElseThrow(() -> new NotFoundException("Package with ID " + id + " was not found"));
  }
}
