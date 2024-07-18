package com.coherentsolutions.pot.insurance.company;

import com.coherentsolutions.pot.insurance.constants.CompanyStatus;
import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import com.coherentsolutions.pot.insurance.specifications.CompanyFilterCriteria;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {
    private final EasyRandom easyRandom = new EasyRandom();

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void testAddCompany() {
        CompanyDTO newCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        CompanyEntity CompanyEntity = CompanyMapper.INSTANCE.toEntity(newCompanyDTO);
        CompanyEntity.setId(UUID.randomUUID());

        when(companyRepository.save(any(CompanyEntity.class))).thenReturn(CompanyEntity);

        CompanyDTO createdCompanyDTO = companyService.addCompany(newCompanyDTO);

        assertEquals(CompanyEntity.getId(), createdCompanyDTO.getId());
        assertEquals(newCompanyDTO.getName(), createdCompanyDTO.getName());
        verify(companyRepository).save(any(CompanyEntity.class));
    }

    @Test
    void testGetAllCompanies() {
        List<CompanyEntity> companyEntities = easyRandom.objects(CompanyEntity.class, 3).toList();
        when(companyRepository.findAll()).thenReturn(companyEntities);

        List<CompanyDTO> result = companyService.getAllCompanies();

        assertEquals(companyEntities.size(), result.size());
        verify(companyRepository).findAll();
    }


    @Test
    void testUpdateCompany() {
        UUID companyId = UUID.randomUUID();
        CompanyDTO originalCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        originalCompanyDTO.setId(companyId);
        CompanyEntity CompanyEntity = CompanyMapper.INSTANCE.toEntity(originalCompanyDTO);

        when(companyRepository.findById(companyId)).thenReturn(java.util.Optional.of(CompanyEntity));
        when(companyRepository.save(any(CompanyEntity.class))).thenReturn(CompanyEntity);

        CompanyDTO updatedCompanyDTO = companyService.updateCompany(originalCompanyDTO, companyId);

        assertEquals(originalCompanyDTO.getName(), updatedCompanyDTO.getName());
        verify(companyRepository).save(CompanyEntity);
    }

    @Test
    void testDeactivateCompany() {
        UUID id = UUID.randomUUID();
        CompanyEntity companyEntity = easyRandom.nextObject(CompanyEntity.class);
        companyEntity.setId(id);
        companyEntity.setStatus(CompanyStatus.ACTIVE);

        when(companyRepository.findById(id)).thenReturn(java.util.Optional.of(companyEntity));
        when(companyRepository.save(any(CompanyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyDTO result = companyService.deactivateCompany(id);

        assertEquals(CompanyStatus.DEACTIVATED, result.getStatus());
        verify(companyRepository).save(companyEntity);
    }

    @Test
    void testGetFilteredSortedPackages() {
        List<CompanyEntity> companyEntities = easyRandom.objects(CompanyEntity.class, 3).toList();
        Page<CompanyEntity> pagedEntities = new PageImpl<>(companyEntities);

        CompanyFilterCriteria filterCriteria = new CompanyFilterCriteria();

        Pageable pageable = PageRequest.of(0, 3, Sort.by("name").ascending());

        when(companyRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagedEntities);

        Page<CompanyDTO> result = companyService.filterAndSortCompany(filterCriteria, pageable);

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(companyEntities.stream().map(CompanyMapper.INSTANCE::toDTO).toList(), result.getContent());

        verify(companyRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
