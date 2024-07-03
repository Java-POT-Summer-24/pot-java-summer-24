package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.exception.BadRequestException;
import com.coherentsolutions.pot.insurance.mapper.PackageMapper;
import com.coherentsolutions.pot.insurance.repository.PackageRepository;
import com.coherentsolutions.pot.insurance.specifications.PackageFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.PackageSpecifications;
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
public class PackageService {

  private final PackageRepository packageRepository;

  public Page<PackageDTO> getFilteredSortedPackages(PackageFilterCriteria criteria, Pageable pageable) {
    Sort defaultSort = Sort.by("name").ascending();

    if (!pageable.getSort().isSorted()) {
      pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
    }

    Specification<PackageEntity> spec = buildSpecification(criteria);
    return packageRepository.findAll(spec, pageable).map(PackageMapper.INSTANCE::entityToDto);
  }

  private Specification<PackageEntity> buildSpecification(PackageFilterCriteria criteria) {
    Specification<PackageEntity> spec = Specification.where(null);

    if (isNotEmpty(criteria.getName())) {
      spec = spec.and(PackageSpecifications.byName(criteria.getName()));
    }
    if (criteria.getStatus() != null) {
      spec = spec.and(PackageSpecifications.byStatus(criteria.getStatus()));
    }
    if (criteria.getStartDate() != null) {
      spec = spec.and(PackageSpecifications.byStartDate(criteria.getStartDate()));
    }
    if (criteria.getEndDate() != null) {
      spec = spec.and(PackageSpecifications.byEndDate(criteria.getEndDate()));
    }
    if (criteria.getPayrollFrequency() != null) {
      spec = spec.and(PackageSpecifications.byPayrollFrequency(criteria.getPayrollFrequency()));
    }

    return spec;
  }

  private boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }

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
