package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import com.coherentsolutions.pot.insurance.specifications.CompanyFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @CacheEvict(value = "companiesList", allEntries = true)
    public CompanyDTO createCompany(@RequestBody CompanyDTO company) {
        return companyService.addCompany(company);
    }

    @GetMapping
    @Cacheable(value = "companiesList")
    public List<CompanyDTO> getAllCompany() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    @Cacheable(value = "company", key = "#id")
    public CompanyDTO getCompanyById(@PathVariable UUID id) {
        return companyService.getCompanyById(id);
    }

    @GetMapping("/filtered")
    @ResponseStatus(HttpStatus.OK)
    public Page<CompanyDTO> getFilteredSortedCompany(
            @ParameterObject CompanyFilterCriteria companyFilterCriteria,
            @ParameterObject Pageable pageable) {
        return companyService.filterAndSortCompany(companyFilterCriteria, pageable);
    }

    @PutMapping("/{id}")
    @Caching(
            evict = {@CacheEvict(value = "companiesList", allEntries = true)},
            put = {@CachePut(value = "company", key = "#id")}
    )
    public CompanyDTO updateCompany(@RequestBody CompanyDTO company, @PathVariable UUID id) {
        return companyService.updateCompany(company, id);
    }

    @DeleteMapping("/{id}")
    @Caching(
            evict = {@CacheEvict(value = "companiesList", allEntries = true)},
            put = {@CachePut(value = "company", key = "#id")}
    )
    public CompanyDTO deactivateCompany(@PathVariable UUID id) {
        return companyService.deactivateCompany(id);
    }
}
