package com.coherentsolutions.pot.insurance.company;

import com.coherentsolutions.pot.insurance.controller.CompanyController;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCompany() {
        CompanyEntity company =  CompanyEntity.builder()
                .name("Test Company")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        when(companyService.addCompany(any(CompanyEntity.class))).thenReturn(company);

        CompanyEntity createdCompany = companyController.createCompany(company);

        assertEquals(company, createdCompany);
        verify(companyService, times(1)).addCompany(company);
    }

    @Test
    void testGetAllCompany() {
        CompanyEntity company1 =  CompanyEntity.builder()
                .name("Test Company 1")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        CompanyEntity company2 =  CompanyEntity.builder()
                .name("Test Company 2")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        List<CompanyEntity> companyList = Arrays.asList(company1, company2);
        when(companyService.getAllCompanies()).thenReturn(companyList);

        List<CompanyEntity> result = companyController.getAllCompany();

        assertEquals(2, result.size());
        verify(companyService, times(1)).getAllCompanies();
    }

    @Test
    void testUpdateCompany() {
        CompanyEntity company = CompanyEntity.builder()
                .name("Test Company")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        when(companyService.updateCompany(anyInt(), any(CompanyEntity.class))).thenReturn(company);

        CompanyEntity updatedCompany = companyController.updateCompany(1, company);

        assertEquals(company.getId(), updatedCompany.getId());
        verify(companyService, times(1)).updateCompany(1, company);
    }

    @Test
    void testDeactivateCompany() {
        CompanyEntity company = CompanyEntity.builder()
                .name("Test Company")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        when(companyService.deactivateCompany(anyInt())).thenReturn(company);

        CompanyEntity deactivateCompany = companyController.deactivateCompany(1);

        assertEquals(company.getStatus(), deactivateCompany.getStatus());
        verify(companyService, times(1)).deactivateCompany(1);
    }
}
