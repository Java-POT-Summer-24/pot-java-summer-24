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
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PackageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String USERNAME = "admin";
  private static final String PASSWORD = "password";

  private String basicAuthHeader() {
    String auth = USERNAME + ":" + PASSWORD;
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
    return "Basic " + new String(encodedAuth);
  }

  @Test
  void testAddPackage() throws Exception {
    PackageDTO packageDTO = new PackageDTO(
        UUID.randomUUID(),
        "Basic Health Package",
        PackageStatus.ACTIVE,
        PackagePayrollFrequency.MONTHLY,
        LocalDate.of(2024, 1, 1),
        LocalDate.of(2025, 1, 1),
        PackageType.DENTAL,
        150.00
    );
    String packageJson = objectMapper.writeValueAsString(packageDTO);

    mockMvc.perform(post("/v1/packages")
            .header("Authorization", basicAuthHeader())
            .contentType(MediaType.APPLICATION_JSON)
            .content(packageJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Basic Health Package"));
  }

  @Test
  void testGetAllPackages() throws Exception {
    mockMvc.perform(get("/v1/packages")
            .header("Authorization", basicAuthHeader()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andReturn();
  }

  @Test
  void testGetPackageById() throws Exception {
    UUID packageId = UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac1a");
    mockMvc.perform(get("/v1/packages/{id}", packageId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(packageId.toString()));
  }

  @Test
  void testUpdatePackage() throws Exception {
    UUID packageId = UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac1a");
    PackageDTO packageDTO = new PackageDTO(
        packageId,
        "Updated Health Package",
        PackageStatus.ACTIVE,
        PackagePayrollFrequency.MONTHLY,
        LocalDate.of(2024, 1, 1),
        LocalDate.of(2026, 1, 1),
        PackageType.MEDICAL,
        200.00
    );
    String packageJson = objectMapper.writeValueAsString(packageDTO);

    mockMvc.perform(put("/v1/packages")
            .header("Authorization", basicAuthHeader())
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
    mockMvc.perform(delete("/v1/packages/{id}", packageId)
            .header("Authorization", basicAuthHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));
  }
}