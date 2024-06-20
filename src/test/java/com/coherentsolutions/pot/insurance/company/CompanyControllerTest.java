package com.coherentsolutions.pot.insurance.company;

import com.coherentsolutions.pot.insurance.controller.CompanyController;
import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    private EasyRandom easyRandom;

    @BeforeEach
    void setUp() {
        easyRandom = new EasyRandom();
    }

    @Test
    void testCreateCompany() {
        CompanyDTO newCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        when(companyService.addCompany(any(CompanyDTO.class))).thenReturn(newCompanyDTO);

        CompanyDTO createdCompanyDTO = companyController.createCompany(newCompanyDTO);

        assertEquals(newCompanyDTO, createdCompanyDTO);
        verify(companyService).addCompany(newCompanyDTO);
    }

    @Test
    void testGetAllCompany() {
        List<CompanyDTO> companyList = easyRandom.objects(CompanyDTO.class, 3).toList();
        when(companyService.getAllCompanies()).thenReturn(companyList);

        List<CompanyDTO> response = companyController.getAllCompany();


        assertEquals(3, response.size());
        verify(companyService).getAllCompanies();
    }

    @Test
    void testUpdateCompany() {
        CompanyDTO originalCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        UUID id = UUID.randomUUID();
        originalCompanyDTO.setId(id);
        CompanyDTO updatedCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        updatedCompanyDTO.setId(id);

        when(companyService.updateCompany(any(CompanyDTO.class))).thenReturn(updatedCompanyDTO);

        CompanyDTO result = companyController.updateCompany(originalCompanyDTO);

        assertEquals(updatedCompanyDTO, result);
        verify(companyService).updateCompany(originalCompanyDTO);
    }

    @Test
    void testDeactivateCompany() {
        CompanyDTO originalCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        UUID id = UUID.randomUUID();
        originalCompanyDTO.setId(id);

        when(companyService.deactivateCompany(id)).thenReturn(originalCompanyDTO);

        CompanyDTO result = companyController.deactivateCompany(id);

        assertEquals(originalCompanyDTO.getStatus(), result.getStatus());
        verify(companyService).deactivateCompany(id);
    }
}
