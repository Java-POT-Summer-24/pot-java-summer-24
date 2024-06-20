package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.Status;
import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.exception.CompanyNotFoundException;
import com.coherentsolutions.pot.insurance.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper = CompanyMapper.INSTANCE;

    public CompanyDTO addCompany(CompanyDTO companyDto) {
        CompanyEntity company = companyMapper.toEntity(companyDto);
        if (company.getId() == null) {
            company.setId(UUID.randomUUID());
        }
        CompanyEntity createCompany = companyRepository.save(company);

        return companyMapper.toDTO(createCompany);
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CompanyDTO updateCompany(CompanyDTO updatedCompany) {
        CompanyEntity company = companyRepository.findById(updatedCompany.getId())
                .orElseThrow(() -> new CompanyNotFoundException("Company with ID " + updatedCompany.getId() + " was not found"));

        companyMapper.updateCompanyFromDTO(updatedCompany, company);
        company = companyRepository.save(company);
        return companyMapper.toDTO(company);
    }

    public CompanyDTO deactivateCompany(UUID id) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setStatus(Status.DEACTIVATED);
                    company = companyRepository.save(company);
                    return companyMapper.toDTO(company);
                })
                .orElseThrow(() -> new CompanyNotFoundException("Company with ID " + id + " was not found"));
    }
}

