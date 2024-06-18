package com.coherentsolutions.pot.insurance.company;

import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGetAllCompany() throws Exception {
        CompanyEntity company1 = CompanyEntity.builder()
                .name("Test Company 1")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        CompanyEntity company2 = CompanyEntity.builder()
                .name("Test Company 2")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        companyRepository.save(company1);
        companyRepository.save(company2);

        mockMvc.perform(get("/company")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(company1.getName())))
                .andExpect(jsonPath("$[1].name", is(company2.getName())));
    }

    @Test
    void testCreateCompany() throws Exception {
        CompanyEntity company =  CompanyEntity.builder()
                .name("Test Company")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();

        mockMvc.perform(post("/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(company)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(company.getName())))
                .andExpect(jsonPath("$.countryCode", is(company.getCountryCode())))
                .andExpect(jsonPath("$.address", is(company.getAddress())))
                .andExpect(jsonPath("$.phoneNumber", is(company.getPhoneNumber())))
                .andExpect(jsonPath("$.email", is(company.getEmail())))
                .andExpect(jsonPath("$.website", is(company.getWebsite())))
                .andExpect(jsonPath("$.status", is(company.getStatus())));
    }

    @Test
    void testUpdateCompany() throws Exception {
        CompanyEntity company =  CompanyEntity.builder()
                .name("Test Company 2")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        company = companyRepository.save(company);
        company.setName("Updated Company");

        mockMvc.perform(put("/company/{id}", company.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(company)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Company")));
    }

    @Test
    void testDeactivateCompany() throws Exception {
        CompanyEntity company = CompanyEntity.builder()
                .name("Test Company 1")
                .countryCode("LT")
                .address("Sauletekio")
                .phoneNumber("+37023423424")
                .email("info@example.com")
                .website("http://www.example.com")
                .status("active")
                .build();
        company = companyRepository.save(company);

        mockMvc.perform(delete("/company/{id}", company.getId()))
                .andExpect(status().isOk());

        Optional<CompanyEntity> updatedCompany = companyRepository.findById(company.getId());
        assert(updatedCompany.isPresent());
        assert(updatedCompany.get().getStatus().equals("deactivate"));
    }
}
