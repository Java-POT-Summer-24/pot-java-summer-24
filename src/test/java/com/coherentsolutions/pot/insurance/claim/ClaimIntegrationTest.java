package com.coherentsolutions.pot.insurance.claim;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.controller.ClaimController;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.mapper.ClaimMapper;
import com.coherentsolutions.pot.insurance.repository.ClaimRepository;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ClaimController.class)
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class ClaimIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ClaimService claimService;

  @MockBean
  private ClaimRepository claimRepository;

  private final EasyRandom easyRandom = new EasyRandom();

  @Test
  void testAddClaim() throws Exception {
    ClaimDTO claimDTO = easyRandom.nextObject(ClaimDTO.class);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);

    Mockito.when(claimRepository.save(any())).thenReturn(claimEntity);
    Mockito.when(claimService.addClaim(any())).thenReturn(claimDTO);

    String claimJson = objectMapper.writeValueAsString(claimDTO);
    mockMvc.perform(post("/v1/claims")
            .contentType(MediaType.APPLICATION_JSON)
            .content(claimJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.claimNumber").value(claimDTO.getClaimNumber()));
  }

  @Test
  void testGetAllClaims() throws Exception {
    List<ClaimEntity> claimsList = easyRandom.objects(ClaimEntity.class, 3).toList();
    List<ClaimDTO> claimDTOs = claimsList.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());

    Mockito.when(claimRepository.findAll()).thenReturn(claimsList);
    Mockito.when(claimService.getAllClaims()).thenReturn(claimDTOs);

    mockMvc.perform(get("/v1/claims"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  void testGetClaimById() throws Exception {
    UUID claimId = UUID.randomUUID();
    ClaimDTO claimDTO = easyRandom.nextObject(ClaimDTO.class);
    claimDTO.setId(claimId);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);

    Mockito.when(claimRepository.findById(claimId)).thenReturn(java.util.Optional.of(claimEntity));
    Mockito.when(claimService.getClaimById(claimId)).thenReturn(claimDTO);

    mockMvc.perform(get("/v1/claims/{id}", claimId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(claimId.toString()));
  }

  @Test
  void testUpdateClaim() throws Exception {
    UUID claimId = UUID.randomUUID();
    ClaimDTO originalClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    originalClaimDTO.setId(claimId);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(originalClaimDTO);

    Mockito.when(claimRepository.findById(claimId)).thenReturn(java.util.Optional.of(claimEntity));
    Mockito.when(claimRepository.save(any())).thenReturn(claimEntity);
    Mockito.when(claimService.updateClaim(eq(claimId), any(ClaimDTO.class))).thenReturn(originalClaimDTO);

    String claimJson = objectMapper.writeValueAsString(originalClaimDTO);
    mockMvc.perform(put("/v1/claims/{id}", claimId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(claimJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.claimNumber").value(originalClaimDTO.getClaimNumber()));
  }

  @Test
  void testDeactivateClaim() throws Exception {
    ClaimDTO claimDTO = easyRandom.nextObject(ClaimDTO.class);
    claimDTO.setStatus(ClaimStatus.DEACTIVATED);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);

    Mockito.when(claimRepository.findById(claimDTO.getId()))
        .thenReturn(java.util.Optional.of(claimEntity));
    Mockito.when(claimRepository.save(any())).thenReturn(claimEntity);
    Mockito.when(claimService.deactivateClaim(claimDTO.getId())).thenReturn(claimDTO);

    mockMvc.perform(delete("/v1/claims/{id}", claimDTO.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));
  }

  @Test
  void testGetAllClaimsByEmployee() throws Exception {
    String employeeUsername = "janedoe";
    List<ClaimEntity> claimsList = easyRandom.objects(ClaimEntity.class, 3)
        .map(claim -> {
          EmployeeEntity employee = new EmployeeEntity();
          employee.setUserName(employeeUsername);
          claim.setEmployee(employee);
          return claim;
        }).collect(Collectors.toList());

    List<ClaimDTO> claimDTOs = claimsList.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());

    Mockito.when(claimRepository.findAllByEmployeeUserName(employeeUsername)).thenReturn(claimsList);
    Mockito.when(claimService.getAllClaimsByEmployee(employeeUsername)).thenReturn(claimDTOs);

    mockMvc.perform(get("/v1/claims/employees/{employee}", employeeUsername))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].employee").value(employeeUsername));
  }

  @Test
  void testGetAllClaimsByCompany() throws Exception {
    String companyName = "Coherent Solutions";
    List<ClaimEntity> claimsList = easyRandom.objects(ClaimEntity.class, 3)
        .map(claim -> {
          if (claim.getCompany() == null) {
            claim.setCompany(new CompanyEntity());
          }
          claim.getCompany().setName(companyName);
          return claim;
        }).collect(Collectors.toList());

    List<ClaimDTO> claimDTOs = claimsList.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());

    Mockito.when(claimRepository.findAllByCompanyName(companyName)).thenReturn(claimsList);
    Mockito.when(claimService.getAllClaimsByCompany(companyName)).thenReturn(claimDTOs);

    mockMvc.perform(get("/v1/claims/companies/{company}", companyName))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }
}