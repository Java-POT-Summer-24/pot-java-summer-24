package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public CompanyDTO createCompany(@RequestBody CompanyDTO company) {
        return companyService.addCompany(company);
    }

    @GetMapping
    public List<CompanyDTO> getAllCompany() {
        return companyService.getAllCompanies();
    }

    @PutMapping
    public CompanyDTO updateCompany(@RequestBody CompanyDTO company) {
        System.out.println("asd");
        return companyService.updateCompany(company);
    }

    @DeleteMapping("/{id}")
    public CompanyDTO deactivateCompany(@PathVariable UUID id) {
        return companyService.deactivateCompany(id);
    }
}
