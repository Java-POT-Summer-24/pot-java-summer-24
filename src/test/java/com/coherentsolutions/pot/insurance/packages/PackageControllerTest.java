package com.coherentsolutions.pot.insurance.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insurance.controller.PackageController;
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.service.PackageService;
import com.coherentsolutions.pot.insurance.specifications.PackageFilterAndSortCriteria;
import com.coherentsolutions.pot.insurance.specifications.PackageFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.SortCriteria;
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

@ExtendWith(MockitoExtension.class)
class PackageControllerTest {

  private final EasyRandom easyRandom = new EasyRandom();

  @Mock
  private PackageService packageService;

  @InjectMocks
  private PackageController packageController;

  @Test
  void testAddPackage() {
    PackageDTO newPackageDTO = easyRandom.nextObject(PackageDTO.class);
    when(packageService.addPackage(any(PackageDTO.class))).thenReturn(newPackageDTO);

    PackageDTO createdPackageDTO = packageController.addPackage(newPackageDTO);

    assertEquals(newPackageDTO, createdPackageDTO);
    verify(packageService).addPackage(newPackageDTO);
  }

  @Test
  void testGetAllPackages() {
    List<PackageDTO> packageList = easyRandom.objects(PackageDTO.class, 3).toList();
    when(packageService.getAllPackages()).thenReturn(packageList);

    List<PackageDTO> result = packageController.getAllPackages();

    assertEquals(3, result.size());
    verify(packageService).getAllPackages();
  }

  @Test
  void testGetPackageById() {
    PackageDTO packageDTO = easyRandom.nextObject(PackageDTO.class);
    UUID id = UUID.randomUUID();
    when(packageService.getPackageById(id)).thenReturn(packageDTO);

    PackageDTO result = packageController.getPackageById(id);

    assertEquals(packageDTO, result);
    verify(packageService).getPackageById(id);
  }

  @Test
  void testUpdatePackage() {
    PackageDTO originalPackageDTO = easyRandom.nextObject(PackageDTO.class);
    PackageDTO updatedPackageDTO = easyRandom.nextObject(PackageDTO.class);
    updatedPackageDTO.setId(originalPackageDTO.getId());

    when(packageService.updatePackage(any(PackageDTO.class))).thenReturn(updatedPackageDTO);

    PackageDTO result = packageController.updatePackage(updatedPackageDTO);

    assertEquals(updatedPackageDTO, result);
    verify(packageService).updatePackage(updatedPackageDTO);
  }

  @Test
  void testDeactivatePackage() {
    UUID id = UUID.randomUUID();
    PackageDTO deactivatedPackageDTO = easyRandom.nextObject(PackageDTO.class);
    when(packageService.deactivatePackage(id)).thenReturn(deactivatedPackageDTO);

    PackageDTO result = packageController.deactivatePackage(id);

    assertEquals(deactivatedPackageDTO, result);
    verify(packageService).deactivatePackage(id);
  }

  @Test
  void testGetFilteredSortedPackages() {
    List<PackageDTO> packageList = easyRandom.objects(PackageDTO.class, 3).toList();
    Page<PackageDTO> pagedPackages = new PageImpl<>(packageList);

    PackageFilterCriteria filterCriteria = new PackageFilterCriteria();
    SortCriteria sortCriteria = new SortCriteria();
    sortCriteria.setField("name");  // Assuming 'name' is a sortable field for packages
    sortCriteria.setDirection(Sort.Direction.ASC);

    PackageFilterAndSortCriteria criteria = new PackageFilterAndSortCriteria();
    criteria.setPackageFilterCriteria(filterCriteria);
    criteria.setSortCriteria(sortCriteria);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(sortCriteria.getDirection(), sortCriteria.getField()));

    when(packageService.getFilteredSortedPackages(any(PackageFilterCriteria.class), any(SortCriteria.class), anyInt(), anyInt()))
        .thenReturn(pagedPackages);

    Page<PackageDTO> result = packageController.getFilteredSortedPackages(criteria, pageable);

    assertNotNull(result);
    assertEquals(3, result.getContent().size());
    assertEquals(packageList, result.getContent());

    verify(packageService).getFilteredSortedPackages(any(PackageFilterCriteria.class), any(SortCriteria.class), anyInt(), anyInt());
  }
}