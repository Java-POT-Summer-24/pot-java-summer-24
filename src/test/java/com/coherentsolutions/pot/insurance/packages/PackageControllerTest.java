package com.coherentsolutions.pot.insurance.packages;

import com.coherentsolutions.pot.insurance.controller.PackageController;
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.service.PackageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.jeasy.random.EasyRandom;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    ResponseEntity<PackageDTO> response = packageController.addPackage(newPackageDTO);
    PackageDTO createdPackageDTO = response.getBody();

    assertEquals(newPackageDTO, createdPackageDTO);
    verify(packageService).addPackage(newPackageDTO);
  }

  @Test
  void testGetAllPackages() {
    List<PackageDTO> packageList = easyRandom.objects(PackageDTO.class, 3).toList();
    when(packageService.getAllPackages()).thenReturn(packageList);

    ResponseEntity<List<PackageDTO>> response = packageController.getAllPackages();
    List<PackageDTO> result = response.getBody();

    assertEquals(3, result.size());
    verify(packageService).getAllPackages();
  }

  @Test
  void testGetPackageById() {
    PackageDTO packageDTO = easyRandom.nextObject(PackageDTO.class);
    UUID id = UUID.randomUUID();
    when(packageService.getPackageById(id)).thenReturn(packageDTO);

    ResponseEntity<PackageDTO> response = packageController.getPackageById(id);
    PackageDTO result = response.getBody();

    assertEquals(packageDTO, result);
    verify(packageService).getPackageById(id);
  }

  @Test
  void testUpdatePackage() {
    PackageDTO originalPackageDTO = easyRandom.nextObject(PackageDTO.class);
    when(packageService.updatePackage(any(PackageDTO.class))).thenReturn(originalPackageDTO);

    ResponseEntity<PackageDTO> response = packageController.updatePackage(originalPackageDTO);
    PackageDTO result = response.getBody();

    assertEquals(originalPackageDTO, result);
    verify(packageService).updatePackage(originalPackageDTO);
  }

  @Test
  void testDeactivatePackage() {
    UUID id = UUID.randomUUID();
    PackageDTO deactivatedPackageDTO = easyRandom.nextObject(PackageDTO.class);
    deactivatedPackageDTO.setId(id);

    when(packageService.deactivatePackage(id)).thenReturn(deactivatedPackageDTO);

    ResponseEntity<PackageDTO> response = packageController.deactivatePackage(id);
    PackageDTO result = response.getBody();

    assertEquals(deactivatedPackageDTO, result);
    verify(packageService).deactivatePackage(id);
  }
}
