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
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ClaimController.class, ClaimService.class})
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration"
})
public class ClaimIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ClaimRepository claimRepository;

  @MockBean
  private EmployeeRepository employeeRepository;

  @MockBean
  private CompanyRepository companyRepository;

  private final EasyRandom easyRandom = new EasyRandom();

  @Test
  void testGetAllClaims() throws Exception {
    List<ClaimEntity> claimEntities = easyRandom.objects(ClaimEntity.class, 3).toList();
    List<ClaimDTO> claimDTOs = claimEntities.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .toList();

    Mockito.when(claimRepository.findAll()).thenReturn(claimEntities);

    mockMvc.perform(get("/v1/claims"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].claimNumber").value(claimDTOs.get(0).getClaimNumber()));
  }

  @Test
  void testAddClaim() throws Exception {
    ClaimDTO claimDTO = easyRandom.nextObject(ClaimDTO.class);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);

    Mockito.when(claimRepository.save(any(ClaimEntity.class))).thenReturn(claimEntity);
    Mockito.when(employeeRepository.findByUserName(any(String.class)))
        .thenReturn(Optional.of(easyRandom.nextObject(EmployeeEntity.class)));
    Mockito.when(companyRepository.findByName(any(String.class)))
        .thenReturn(Optional.of(easyRandom.nextObject(CompanyEntity.class)));

    String claimJson = objectMapper.writeValueAsString(claimDTO);
    mockMvc.perform(post("/v1/claims")
            .contentType(MediaType.APPLICATION_JSON)
            .content(claimJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.claimNumber").value(claimDTO.getClaimNumber()));
  }

  @Test
  void testGetClaimById() throws Exception {
    UUID claimId = UUID.randomUUID();
    ClaimDTO claimDTO = easyRandom.nextObject(ClaimDTO.class);
    claimDTO.setId(claimId);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);

    Mockito.when(claimRepository.findById(claimId)).thenReturn(Optional.of(claimEntity));

    mockMvc.perform(get("/v1/claims/{id}", claimId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.claimNumber").value(claimDTO.getClaimNumber()));
  }

  @Test
  void testUpdateClaim() throws Exception {
    UUID claimId = UUID.randomUUID();
    ClaimDTO originalClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    originalClaimDTO.setId(claimId);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(originalClaimDTO);

    Mockito.when(claimRepository.findById(eq(claimId))).thenReturn(Optional.of(claimEntity));
    Mockito.when(claimRepository.save(any(ClaimEntity.class))).thenReturn(claimEntity);

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
    UUID claimId = UUID.randomUUID();
    ClaimDTO claimDTO = easyRandom.nextObject(ClaimDTO.class);
    claimDTO.setId(claimId);
    claimDTO.setStatus(ClaimStatus.DEACTIVATED);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(claimDTO);

    Mockito.when(claimRepository.findById(claimId))
        .thenReturn(Optional.of(claimEntity));
    Mockito.when(claimRepository.save(any(ClaimEntity.class))).thenReturn(claimEntity);

    mockMvc.perform(delete("/v1/claims/{id}", claimId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));
  }

  @Test
  void testGetAllClaimsByEmployeeUserName() throws Exception {
    String employee = "janedoe";

    List<ClaimEntity> claimEntities = easyRandom.objects(ClaimEntity.class, 3)
        .peek(entity -> entity.getEmployee().setUserName(employee)).toList();
    List<ClaimDTO> claimDTOs = claimEntities.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .peek(dto -> dto.setEmployee(employee))
        .toList();

    Mockito.when(claimRepository.findAllByEmployeeUserName(employee)).thenReturn(claimEntities);

    mockMvc.perform(get("/v1/claims/employees/{employeeUserName}", employee))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].employee").value(employee));
  }

  @Test
  void testGetAllClaimsByCompanyName() throws Exception {
    String company = "ISSoft";

    List<ClaimEntity> claimEntities = easyRandom.objects(ClaimEntity.class, 3).peek(claimEntity -> {
      claimEntity.getCompany().setName(company);
    }).toList();

    List<ClaimDTO> claimDTOs = claimEntities.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .peek(dto -> dto.setCompany(company))
        .toList();

    Mockito.when(claimRepository.findAllByCompanyName(company)).thenReturn(claimEntities);
    Mockito.when(companyRepository.findByName(any(String.class)))
        .thenReturn(Optional.of(easyRandom.nextObject(CompanyEntity.class)));

    mockMvc.perform(get("/v1/claims/companies/{companyName}", company))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].company").value(company));
  }

  @Test
  void testGetFilteredSortedClaims() throws Exception {
    List<ClaimEntity> claimEntities = easyRandom.objects(ClaimEntity.class, 3).toList();
    List<ClaimDTO> claimDTOs = claimEntities.stream()
        .map(ClaimMapper.INSTANCE::entityToDto)
        .toList();

    Page<ClaimDTO> pagedClaims = new PageImpl<>(claimDTOs, PageRequest.of(0, 3), claimDTOs.size());

    Mockito.when(claimRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(claimEntities));

    mockMvc.perform(get("/v1/claims/filtered")
            .param("claimNumber", "12345")
            .param("status", "ACTIVE")
            .param("page", "0")
            .param("size", "3")
            .param("sort", "dateOfService,desc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3))
        .andExpect(jsonPath("$.content[0].claimNumber").value(claimDTOs.get(0).getClaimNumber()))
        .andExpect(jsonPath("$.content[1].claimNumber").value(claimDTOs.get(1).getClaimNumber()))
        .andExpect(jsonPath("$.content[2].claimNumber").value(claimDTOs.get(2).getClaimNumber()));
  }
}

