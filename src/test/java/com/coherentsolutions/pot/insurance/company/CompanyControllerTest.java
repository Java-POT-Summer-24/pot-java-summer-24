package com.coherentsolutions.pot.insurance.company;

import com.coherentsolutions.pot.insurance.controller.CompanyController;
import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin")
    void testCreateCompany() throws Exception {
        CompanyDTO newCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        when(companyService.addCompany(any(CompanyDTO.class))).thenReturn(newCompanyDTO);

        mockMvc.perform(post("/v1/companies")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompanyDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(newCompanyDTO)));

        verify(companyService).addCompany(newCompanyDTO);
    }

    @Test
    @WithMockUser(username = "admin")
    void testUpdateCompany() throws Exception {
        CompanyDTO originalCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        UUID id = UUID.randomUUID();
        originalCompanyDTO.setId(id);
        CompanyDTO updatedCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        updatedCompanyDTO.setId(id);

        when(companyService.updateCompany(any(CompanyDTO.class))).thenReturn(updatedCompanyDTO);

        mockMvc.perform(put("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalCompanyDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedCompanyDTO)));

        verify(companyService).updateCompany(originalCompanyDTO);
    }

    @Test
    @WithMockUser(username = "admin")
    void testGetAllCompany() throws Exception {
        List<CompanyDTO> companyList = easyRandom.objects(CompanyDTO.class, 3).toList();
        when(companyService.getAllCompanies()).thenReturn(companyList);

        mockMvc.perform(get("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(companyList)));

        verify(companyService).getAllCompanies();
    }

    @Test
    @WithMockUser(username = "admin")
    void testDeactivateCompany() throws Exception {
        UUID id = UUID.randomUUID();
        CompanyDTO originalCompanyDTO = easyRandom.nextObject(CompanyDTO.class);
        originalCompanyDTO.setId(id);

        when(companyService.deactivateCompany(id)).thenReturn(originalCompanyDTO);

        mockMvc.perform(delete("/companies/{id}", id)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Adding CSRF token
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(originalCompanyDTO)));

        verify(companyService).deactivateCompany(id);
    }
}
