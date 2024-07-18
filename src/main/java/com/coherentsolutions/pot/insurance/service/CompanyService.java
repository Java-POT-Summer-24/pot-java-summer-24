package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.CompanyStatus;
import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;

import com.coherentsolutions.pot.insurance.specifications.CompanyFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.CompanySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Page<CompanyDTO> filterAndSortCompany(CompanyFilterCriteria companyFilterCriteria, Pageable pageable) {
        Sort defaultSort = Sort.by("name").ascending();

        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        Specification<CompanyEntity> spec = buildSpecification(companyFilterCriteria);

        return companyRepository.findAll(spec, pageable).map(CompanyMapper.INSTANCE::toDTO);
    }

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

    public CompanyDTO getCompanyById (UUID id) {
        CompanyEntity company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company with ID " + id + " not found"));
        return CompanyMapper.INSTANCE.toDTO(company);
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

    private Specification<CompanyEntity> buildSpecification(CompanyFilterCriteria companyFilterCriteria) {
        Specification<CompanyEntity> spec = Specification.where(null);

        if (isNotEmpty(companyFilterCriteria.getName())) {
            spec = spec.and(CompanySpecifications.byName(companyFilterCriteria.getName()));
        }
        if (isNotEmpty(companyFilterCriteria.getWebsite())) {
            spec = spec.and(CompanySpecifications.byWebsite(companyFilterCriteria.getWebsite()));
        }
        if (isNotEmpty(companyFilterCriteria.getCountryCode())) {
            spec = spec.and(CompanySpecifications.byCountryCode(companyFilterCriteria.getCountryCode()));
        }
        if (isNotEmpty(companyFilterCriteria.getEmail())) {
            spec = spec.and(CompanySpecifications.byEmail(companyFilterCriteria.getEmail()));
        }
        if (companyFilterCriteria.getStatus() != null) {
            spec = spec.and(CompanySpecifications.byStatus(companyFilterCriteria.getStatus()));
        }

        return spec;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}

