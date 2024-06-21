package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.exception.BadRequestException;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
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
        .map(PackageMapper.INSTANCE::packageToPackageDTO)
        .collect(Collectors.toList());
  }

  public PackageDTO getPackageById(UUID id) {
    PackageEntity packageEntity = packageRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Package with ID " + id + " not found"));
    return PackageMapper.INSTANCE.packageToPackageDTO(packageEntity);
  }

  public PackageDTO addPackage(PackageDTO packageDTO) {
    if (packageRepository.existsByName(packageDTO.getName())) {
      throw new BadRequestException("Package with name " + packageDTO.getName() + " already exists.");
    }
    PackageEntity packageEntity = PackageMapper.INSTANCE.packageDTOToPackage(packageDTO);
    packageEntity.setId(UUID.randomUUID());
    packageEntity = packageRepository.save(packageEntity);
    return PackageMapper.INSTANCE.packageToPackageDTO(packageEntity);
  }

  public PackageDTO updatePackage(UUID id, PackageDTO packageDTO) {
    if (!packageRepository.existsById(id)) {
      throw new NotFoundException("Package with ID " + id + " not found");
    }

    PackageEntity existingPackage = packageRepository.findById(id).orElse(null);
    if (existingPackage != null && !existingPackage.getName().equals(packageDTO.getName()) && packageRepository.existsByName(packageDTO.getName())) {
      throw new BadRequestException("Package with name " + packageDTO.getName() + " already exists under a different ID.");
    }

    PackageEntity packageEntity = PackageMapper.INSTANCE.packageDTOToPackage(packageDTO);
    packageEntity.setId(id);
    packageEntity = packageRepository.save(packageEntity);
    return PackageMapper.INSTANCE.packageToPackageDTO(packageEntity);
  }

  public void deletePackage(UUID id) {
    if (!packageRepository.existsById(id)) {
      throw new NotFoundException("Package with ID " + id + " not found");
    }
    packageRepository.deleteById(id);
  }
}
