package com.coherentsolutions.pot.insurance.packages;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.PackagePayrollFrequency;
import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.constants.PackageType;
import com.coherentsolutions.pot.insurance.controller.PackageController;
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.service.PackageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PackageController.class)
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class PackageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PackageService packageService;

  @Test
  void testAddPackage() throws Exception {
    PackageDTO packageDTO = PackageDTO.builder()
        .id(UUID.randomUUID())
        .name("Basic Health Package")
        .status(PackageStatus.ACTIVE)
        .payrollFrequency(PackagePayrollFrequency.MONTHLY)
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .type(PackageType.DENTAL)
        .contributions(150.00)
        .build();
    String packageJson = objectMapper.writeValueAsString(packageDTO);

    Mockito.when(packageService.addPackage(Mockito.any())).thenReturn(packageDTO);

    mockMvc.perform(post("/v1/packages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(packageJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Basic Health Package"));
  }

  @Test
  void testGetAllPackages() throws Exception {
    mockMvc.perform(get("/v1/packages"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andReturn();
  }

  @Test
  void testGetPackageById() throws Exception {
    UUID packageId = UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac1a");
    PackageDTO packageDTO = PackageDTO.builder()
        .id(packageId)
        .name("Basic Health Package")
        .status(PackageStatus.ACTIVE)
        .payrollFrequency(PackagePayrollFrequency.MONTHLY)
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .type(PackageType.DENTAL)
        .contributions(150.00)
        .build();

    Mockito.when(packageService.getPackageById(packageId)).thenReturn(packageDTO);

    mockMvc.perform(get("/v1/packages/{id}", packageId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(packageId.toString()));
  }

  @Test
  void testUpdatePackage() throws Exception {
    UUID packageId = UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac1a");
    PackageDTO packageDTO = PackageDTO.builder()
        .id(packageId)
        .name("Updated Health Package")
        .status(PackageStatus.ACTIVE)
        .payrollFrequency(PackagePayrollFrequency.MONTHLY)
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2026, 1, 1))
        .type(PackageType.MEDICAL)
        .contributions(200.00)
        .build();
    String packageJson = objectMapper.writeValueAsString(packageDTO);

    Mockito.when(packageService.updatePackage(Mockito.any())).thenReturn(packageDTO);

    mockMvc.perform(put("/v1/packages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(packageJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Health Package"))
        .andReturn();
  }

  @Test
  void testDeactivatePackage() throws Exception {
    UUID packageId = UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac1a");
    PackageDTO packageDTO = PackageDTO.builder()
        .id(packageId)
        .name("Basic Health Package")
        .status(PackageStatus.DEACTIVATED)
        .payrollFrequency(PackagePayrollFrequency.MONTHLY)
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .type(PackageType.DENTAL)
        .contributions(150.00)
        .build();

    Mockito.when(packageService.deactivatePackage(packageId)).thenReturn(packageDTO);

    mockMvc.perform(delete("/v1/packages/{id}", packageId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));
  }
}