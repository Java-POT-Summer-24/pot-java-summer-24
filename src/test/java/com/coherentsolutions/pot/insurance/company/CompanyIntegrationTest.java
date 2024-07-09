package com.coherentsolutions.pot.insurance.company;

import com.coherentsolutions.pot.insurance.constants.CompanyStatus;
import com.coherentsolutions.pot.insurance.controller.CompanyController;
import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


import org.springframework.data.domain.Pageable;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CompanyController.class, CompanyService.class})
class CompanyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyRepository companyRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @WithMockUser(username = "admin")
    void testGetFilteredSortedCompanies() throws Exception {
        List<CompanyEntity> companyEntities = easyRandom.objects(CompanyEntity.class, 3).toList();
        List<CompanyDTO> companyDTOS = companyEntities.stream()
                .map(CompanyMapper.INSTANCE::toDTO)
                .toList();

        Page<CompanyDTO> pagedCompany = new PageImpl<>(companyDTOS, PageRequest.of(0, 3), companyDTOS.size());

        Mockito.when(companyRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(companyEntities));

        mockMvc.perform(get("/v1/companies/filtered")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", "Company A")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "name,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].name").value(companyDTOS.get(0).getName()))
                .andExpect(jsonPath("$.content[1].name").value(companyDTOS.get(1).getName()))
                .andExpect(jsonPath("$.content[2].name").value(companyDTOS.get(2).getName()));
    }


    @Test
    @WithMockUser(username = "admin")
    void testCreateCompany() throws Exception {
        CompanyDTO companyDTO = easyRandom.nextObject(CompanyDTO.class);
        CompanyEntity companyEntity = CompanyMapper.INSTANCE.toEntity(companyDTO);

        Mockito.when(companyRepository.save(any(CompanyEntity.class))).thenReturn(companyEntity);

        String packageJson = objectMapper.writeValueAsString(companyDTO);
        mockMvc.perform(post("/v1/companies")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(packageJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(companyDTO.getName()));
    }

    @Test
    @WithMockUser(username = "admin")
    void testGetAllCompany() throws Exception {
        List<CompanyEntity> companyEntities = easyRandom.objects(CompanyEntity.class, 3).toList();

        Mockito.when(companyRepository.findAll()).thenReturn(companyEntities);

        mockMvc.perform(get("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value(companyEntities.get(0).getName()));
    }

    @Test
    @WithMockUser(username = "admin")
    void testUpdateCompany() throws Exception {
        UUID companyId = UUID.randomUUID();
        CompanyDTO originalCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        originalCompanyDTO.setId(companyId);
        CompanyEntity companyEntity = CompanyMapper.INSTANCE.toEntity(originalCompanyDTO);

        Mockito.when(companyRepository.findById(eq(companyId))).thenReturn(Optional.of(companyEntity));
        Mockito.when(companyRepository.save(any(CompanyEntity.class))).thenReturn(companyEntity);

        String packageJson = objectMapper.writeValueAsString(originalCompanyDTO);
        mockMvc.perform(put("/v1/companies/{id}", companyId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(packageJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(originalCompanyDTO.getName()));
    }

    @Test
    @WithMockUser(username = "admin")
    void testDeactivateCompany() throws Exception {
        UUID companyId = UUID.randomUUID();
        CompanyDTO companyDTO = easyRandom.nextObject(CompanyDTO.class);
        companyDTO.setId(companyId);
        companyDTO.setStatus(CompanyStatus.DEACTIVATED);
        CompanyEntity companyEntity = CompanyMapper.INSTANCE.toEntity(companyDTO);

        Mockito.when(companyRepository.findById(companyId))
                .thenReturn(java.util.Optional.of(companyEntity));
        Mockito.when(companyRepository.save(any(CompanyEntity.class))).thenReturn(companyEntity);

        mockMvc.perform(delete("/v1/companies/{id}", companyId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEACTIVATED"));
    }
}
