package com.coherentsolutions.pot.insurance.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.mapper.PackageMapper;
import com.coherentsolutions.pot.insurance.repository.PackageRepository;
import com.coherentsolutions.pot.insurance.service.PackageService;
import com.coherentsolutions.pot.insurance.specifications.PackageFilterCriteria;
import java.util.List;
import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class PackageServiceTest {

  private final EasyRandom easyRandom = new EasyRandom();

  @Mock
  private PackageRepository packageRepository;

  @InjectMocks
  private PackageService packageService;

  @Test
  void testAddPackage() {
    PackageDTO newPackageDTO = easyRandom.nextObject(PackageDTO.class);
    PackageEntity packageEntity = PackageMapper.INSTANCE.dtoToEntity(newPackageDTO);
    packageEntity.setId(UUID.randomUUID());

    when(packageRepository.save(any(PackageEntity.class))).thenReturn(packageEntity);

    PackageDTO createdPackageDTO = packageService.addPackage(newPackageDTO);

    assertEquals(packageEntity.getId(), createdPackageDTO.getId());
    assertEquals(newPackageDTO.getName(), createdPackageDTO.getName());
    verify(packageRepository).save(any(PackageEntity.class));
  }

  @Test
  void testGetAllPackages() {
    List<PackageEntity> packageEntities = easyRandom.objects(PackageEntity.class, 3).toList();
    when(packageRepository.findAll()).thenReturn(packageEntities);

    List<PackageDTO> result = packageService.getAllPackages();

    assertEquals(packageEntities.size(), result.size());
    verify(packageRepository).findAll();
  }

  @Test
  void testGetPackageById() {
    UUID id = UUID.randomUUID();
    PackageEntity packageEntity = easyRandom.nextObject(PackageEntity.class);
    packageEntity.setId(id);
    when(packageRepository.findById(id)).thenReturn(java.util.Optional.of(packageEntity));

    PackageDTO result = packageService.getPackageById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(packageRepository).findById(id);
  }

  @Test
  void testUpdatePackage() {
    UUID packageId = UUID.randomUUID();
    PackageDTO originalPackageDTO = easyRandom.nextObject(PackageDTO.class);
    originalPackageDTO.setId(packageId);
    PackageEntity packageEntity = PackageMapper.INSTANCE.dtoToEntity(originalPackageDTO);

    when(packageRepository.findById(packageId)).thenReturn(java.util.Optional.of(packageEntity));
    when(packageRepository.save(any(PackageEntity.class))).thenReturn(packageEntity);

    PackageDTO updatedPackageDTO = packageService.updatePackage(packageId, originalPackageDTO);

    assertEquals(originalPackageDTO.getName(), updatedPackageDTO.getName());
    verify(packageRepository).save(packageEntity);
  }

  @Test
  void testDeactivatePackage() {
    UUID id = UUID.randomUUID();
    PackageEntity packageEntity = easyRandom.nextObject(PackageEntity.class);
    packageEntity.setId(id);
    packageEntity.setStatus(PackageStatus.ACTIVE);

    when(packageRepository.findById(id)).thenReturn(java.util.Optional.of(packageEntity));
    when(packageRepository.save(any(PackageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PackageDTO result = packageService.deactivatePackage(id);

    assertEquals(PackageStatus.DEACTIVATED, result.getStatus());
    verify(packageRepository).save(packageEntity);
  }

  @Test
  void testGetFilteredSortedPackages() {
    List<PackageEntity> packageEntities = easyRandom.objects(PackageEntity.class, 3).toList();
    Page<PackageEntity> pagedEntities = new PageImpl<>(packageEntities);

    PackageFilterCriteria filterCriteria = new PackageFilterCriteria();
    Pageable pageable = PageRequest.of(0, 3, Sort.by("name").ascending());

    when(packageRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagedEntities);

    Page<PackageDTO> result = packageService.getFilteredSortedPackages(filterCriteria, pageable);

    assertNotNull(result);
    assertEquals(3, result.getContent().size());
    assertEquals(packageEntities.stream().map(PackageMapper.INSTANCE::entityToDto).toList(), result.getContent());

    verify(packageRepository).findAll(any(Specification.class), any(Pageable.class));
  }
}