package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.exception.ResourceNotFoundException;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyEntity addCompany(CompanyEntity company) {
        return companyRepository.save(company);
    }

    public List<CompanyEntity> getAllCompanies() {
        return companyRepository.findAll();
    }

    public CompanyEntity updateCompany(Integer id, CompanyEntity updatedCompany) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setName(updatedCompany.getName());
                    company.setCountryCode(updatedCompany.getCountryCode());
                    company.setAddress(updatedCompany.getAddress());
                    company.setPhoneNumber(updatedCompany.getPhoneNumber());
                    company.setEmail(updatedCompany.getEmail());
                    company.setWebsite(updatedCompany.getWebsite());
                    return companyRepository.save(company);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Company with ID " + id + " was not found"));


    }

    public CompanyEntity deactivateCompany(Integer id) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setStatus("deactivate");
                    return companyRepository.save(company);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Company with ID " + id + " was not found"));
    }
}

