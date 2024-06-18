package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public CompanyEntity createCompany(@RequestBody CompanyEntity company) {
        return companyService.addCompany(company);
    }

    @GetMapping
    public List<CompanyEntity> getAllCompany() {
        return companyService.getAllCompanies();
    }

    @PutMapping("/{id}")
    public CompanyEntity updateCompany(@PathVariable Integer id, @RequestBody CompanyEntity company) {
        return companyService.updateCompany(id, company);
    }

    @DeleteMapping("/{id}")
    public CompanyEntity deactivateCompany(@PathVariable Integer id) {
        return companyService.deactivateCompany(id);
    }
}
