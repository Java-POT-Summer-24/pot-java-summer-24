package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.service.PackageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/packages")
@RequiredArgsConstructor
public class PackageController {

  private final PackageService packageService;

  @GetMapping
  public ResponseEntity<List<PackageDTO>> getAllPackages() {
    return ResponseEntity.ok(packageService.getAllPackages());
  }

  @GetMapping("/{id}")
  public ResponseEntity<PackageDTO> getPackageById(@PathVariable UUID id) {
    return ResponseEntity.ok(packageService.getPackageById(id));
  }

  @PostMapping
  public ResponseEntity<PackageDTO> addPackage(@Valid @RequestBody PackageDTO packageDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(packageService.addPackage(packageDTO));
  }

  @PutMapping
  public ResponseEntity<PackageDTO> updatePackage(@Valid @RequestBody PackageDTO packageDTO) {
    return ResponseEntity.ok(packageService.updatePackage(packageDTO));
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<PackageDTO> deactivatePackage(@PathVariable UUID id) {
    return ResponseEntity.ok(packageService.deactivatePackage(id));
  }
}
