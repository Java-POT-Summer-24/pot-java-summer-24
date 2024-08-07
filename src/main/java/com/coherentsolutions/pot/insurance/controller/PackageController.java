package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.service.PackageService;
import com.coherentsolutions.pot.insurance.specifications.PackageFilterCriteria;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/packages")
@RequiredArgsConstructor
public class PackageController {

  private final PackageService packageService;

  @GetMapping("/filtered")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canGetFilteredSortedPackages(authentication)")
  public Page<PackageDTO> getFilteredSortedPackages(
      @ParameterObject PackageFilterCriteria criteria,
      @ParameterObject Pageable pageable) {
    return packageService.getFilteredSortedPackages(criteria, pageable);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canGetAllPackages(authentication)")
  @Cacheable(value = "packagesList")
  public List<PackageDTO> getAllPackages() {
    return packageService.getAllPackages();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canAccessPackage(authentication, #id)")
  @Cacheable(value = "package", key = "#id")
  public PackageDTO getPackageById(@PathVariable UUID id) {
    return packageService.getPackageById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.canAddPackage(authentication)")
  @CacheEvict(value = "packagesList", allEntries = true)
  public PackageDTO addPackage(@Valid @RequestBody PackageDTO packageDTO) {
    return packageService.addPackage(packageDTO);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canUpdatePackage(authentication, #id)")
  @Caching(
      evict = {@CacheEvict(value = "packagesList", allEntries = true)},
      put = {@CachePut(value = "package", key = "#id")}
  )
  public PackageDTO updatePackage(@PathVariable UUID id, @Valid @RequestBody PackageDTO packageDTO) {
    return packageService.updatePackage(id, packageDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.canDeletePackage(authentication, #id)")
  @Caching(
      evict = {@CacheEvict(value = "packagesList", allEntries = true)},
      put = {@CachePut(value = "package", key = "#id")}
  )
  public PackageDTO deactivatePackage(@PathVariable UUID id) {
    return packageService.deactivatePackage(id);
  }
}
