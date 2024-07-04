package com.coherentsolutions.pot.insurance.claim;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.ClaimPlan;
import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.controller.ClaimController;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.repository.ClaimRepository;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.util.ClaimNumberGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  @Test
  void testAddClaim() throws Exception {
    ClaimDTO claimDTO = createBasicClaimDTO().build();
    ClaimEntity claimEntity = createBasicClaimEntity(claimDTO);

    Mockito.when(claimRepository.save(Mockito.any(ClaimEntity.class))).thenReturn(claimEntity);
    Mockito.when(claimService.addClaim(Mockito.any(ClaimDTO.class))).thenReturn(claimDTO);

    String claimJson = objectMapper.writeValueAsString(claimDTO);
    mockMvc.perform(post("/v1/claims")
            .contentType(MediaType.APPLICATION_JSON)
            .content(claimJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.claimNumber").value(claimDTO.getClaimNumber()));
  }

  @Test
  void testGetAllClaims() throws Exception {
    List<ClaimEntity> claimsList = List.of(
        createBasicClaimEntity(createBasicClaimDTO().build()),
        createBasicClaimEntity(createBasicClaimDTO().build()),
        createBasicClaimEntity(createBasicClaimDTO().build())
    );

    Mockito.when(claimRepository.findAll()).thenReturn(claimsList);

    mockMvc.perform(get("/v1/claims"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andReturn();
  }

  @Test
  void testGetClaimById() throws Exception {
    UUID claimId = UUID.randomUUID();
    ClaimDTO claimDTO = createBasicClaimDTO().id(claimId).build();
    ClaimEntity claimEntity = createBasicClaimEntity(claimDTO);

    Mockito.when(claimRepository.findById(claimId)).thenReturn(Optional.of(claimEntity));
    Mockito.when(claimService.getClaimById(claimId)).thenReturn(claimDTO);

    mockMvc.perform(get("/v1/claims/{id}", claimId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(claimId.toString()));
  }

  @Test
  void testUpdateClaim() throws Exception {
    UUID claimId = UUID.randomUUID();
    ClaimDTO originalClaimDTO = createBasicClaimDTO().id(claimId).build();
    ClaimDTO updatedClaimDTO = createBasicClaimDTO().id(claimId).claimNumber("UPDATED-9999XYZ").build();
    ClaimEntity claimEntity = createBasicClaimEntity(updatedClaimDTO);

    Mockito.when(claimRepository.findById(claimId)).thenReturn(Optional.of(claimEntity));
    Mockito.when(claimRepository.save(Mockito.any(ClaimEntity.class))).thenReturn(claimEntity);
    Mockito.when(claimService.updateClaim(Mockito.any(ClaimDTO.class))).thenReturn(updatedClaimDTO);

    String claimJson = objectMapper.writeValueAsString(updatedClaimDTO);
    mockMvc.perform(put("/v1/claims")
            .contentType(MediaType.APPLICATION_JSON)
            .content(claimJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.claimNumber").value("UPDATED-9999XYZ"))
        .andReturn();
  }

  @Test
  void testDeactivateClaim() throws Exception {
    UUID claimId = UUID.randomUUID();
    ClaimDTO claimDTO = createBasicClaimDTO().id(claimId).status(ClaimStatus.DEACTIVATED).build();
    ClaimEntity claimEntity = createBasicClaimEntity(claimDTO);

    Mockito.when(claimRepository.findById(claimId)).thenReturn(Optional.of(claimEntity));
    Mockito.when(claimRepository.save(Mockito.any(ClaimEntity.class))).thenReturn(claimEntity);
    Mockito.when(claimService.deactivateClaim(claimId)).thenReturn(claimDTO);

    mockMvc.perform(delete("/v1/claims/{id}", claimId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));
  }

  @Test
  void testGetClaimsByConsumer() throws Exception {
    String consumer = "janedoe";
    List<ClaimEntity> claimsList = List.of(
        createBasicClaimEntity(createBasicClaimDTO().consumer(consumer).build()),
        createBasicClaimEntity(createBasicClaimDTO().consumer(consumer).build()),
        createBasicClaimEntity(createBasicClaimDTO().consumer(consumer).build())
    );
    List<ClaimDTO> claimsDTOList = List.of(
        createBasicClaimDTO().consumer(consumer).build(),
        createBasicClaimDTO().consumer(consumer).build(),
        createBasicClaimDTO().consumer(consumer).build()
    );

    Mockito.when(claimRepository.findByConsumer_UserName(consumer)).thenReturn(claimsList);
    Mockito.when(claimService.getClaimsByConsumer(consumer)).thenReturn(claimsDTOList);

    mockMvc.perform(get("/v1/claims/consumer/{consumer}", consumer))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  void testGetClaimsByEmployer() throws Exception {
    String employer = "Coherent Solutions";
    List<ClaimEntity> claimsList = List.of(
        createBasicClaimEntity(createBasicClaimDTO().employer(employer).build()),
        createBasicClaimEntity(createBasicClaimDTO().employer(employer).build()),
        createBasicClaimEntity(createBasicClaimDTO().employer(employer).build())
    );
    List<ClaimDTO> claimsDTOList = List.of(
        createBasicClaimDTO().employer(employer).build(),
        createBasicClaimDTO().employer(employer).build(),
        createBasicClaimDTO().employer(employer).build()
    );

    Mockito.when(claimRepository.findByEmployer_Name(employer)).thenReturn(claimsList);
    Mockito.when(claimService.getClaimsByEmployer(employer)).thenReturn(claimsDTOList);

    mockMvc.perform(get("/v1/claims/employer/{employer}", employer))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  private ClaimDTO.ClaimDTOBuilder createBasicClaimDTO() {
    return ClaimDTO.builder()
        .id(UUID.randomUUID())
        .claimNumber(ClaimNumberGenerator.generate())
        .consumer("janedoe")
        .employer("Coherent Solutions")
        .dateOfService(LocalDate.of(2023, 7, 1))
        .plan(ClaimPlan.DENTAL)
        .amount(150.75)
        .status(ClaimStatus.HOLD);
  }

  private ClaimEntity createBasicClaimEntity(ClaimDTO claimDTO) {
    EmployeeEntity consumer = new EmployeeEntity();
    consumer.setUserName(claimDTO.getConsumer());

    CompanyEntity employer = new CompanyEntity();
    employer.setName(claimDTO.getEmployer());

    ClaimEntity claimEntity = new ClaimEntity();
    claimEntity.setId(claimDTO.getId());
    claimEntity.setClaimNumber(claimDTO.getClaimNumber());
    claimEntity.setConsumer(consumer);
    claimEntity.setEmployer(employer);
    claimEntity.setDateOfService(claimDTO.getDateOfService());
    claimEntity.setPlan(claimDTO.getPlan());
    claimEntity.setAmount(claimDTO.getAmount());
    claimEntity.setStatus(claimDTO.getStatus());
    return claimEntity;
  }
}