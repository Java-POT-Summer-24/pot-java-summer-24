package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.CompanyStatus;
import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
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


    public CompanyDTO addCompany(CompanyDTO companyDto) {
        CompanyEntity company = CompanyMapper.INSTANCE.toEntity(companyDto);
        CompanyEntity createCompany = companyRepository.save(company);

        return CompanyMapper.INSTANCE.toDTO(createCompany);
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(CompanyMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());

    }

    public CompanyDTO updateCompany(CompanyDTO updatedCompany, UUID id) {
        CompanyEntity company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company with ID " + id + " was not found"));

        CompanyMapper.INSTANCE.updateCompanyFromDTO(updatedCompany, company);
        company = companyRepository.save(company);
        return CompanyMapper.INSTANCE.toDTO(company);
    }

    public CompanyDTO deactivateCompany(UUID id) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setStatus(CompanyStatus.DEACTIVATED);
                    company = companyRepository.save(company);
                    return CompanyMapper.INSTANCE.toDTO(company);
                })
                .orElseThrow(() -> new NotFoundException("Company with ID " + id + " was not found"));
    }
}

