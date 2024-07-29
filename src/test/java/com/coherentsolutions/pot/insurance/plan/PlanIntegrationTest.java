package com.coherentsolutions.pot.insurance.plan;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import com.coherentsolutions.pot.insurance.mapper.PlanMapper;
import com.coherentsolutions.pot.insurance.repository.PackageRepository;
import com.coherentsolutions.pot.insurance.repository.PlanRepository;
import java.util.List;
import java.util.Optional;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.jeasy.random.EasyRandom;
import com.coherentsolutions.pot.insurance.controller.PlanController;
import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.service.PlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({PlanController.class, PlanService.class})
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration"
})
public class PlanIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PlanRepository planRepository;

  @MockBean
  private PackageRepository packageRepository;

  private final EasyRandom easyRandom = new EasyRandom();

  @Test
  void testAddPlan() throws Exception {
    PlanDTO newPlanDTO = easyRandom.nextObject(PlanDTO.class);
    newPlanDTO.setTotalLimit(200);
    newPlanDTO.setPackageId(UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac2d"));
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(newPlanDTO);

    Mockito.when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);
    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setId(newPlanDTO.getPackageId());
    packageEntity.setContributions(1000);
    when(packageRepository.findById(newPlanDTO.getPackageId())).thenReturn(Optional.of(packageEntity));

    mockMvc.perform(post("/v1/plans")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newPlanDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.planName").value(newPlanDTO.getPlanName()));

  }

  @Test
  void testGetAllPlans() throws Exception {
    List<PlanEntity> planEntities = easyRandom.objects(PlanEntity.class, 3).toList();

    Mockito.when(planRepository.findAll()).thenReturn(planEntities);

    mockMvc.perform(get("/v1/plans"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].planName").value(planEntities.get(0).getPlanName()));

  }

  @Test
  void testGetPlanById() throws Exception {
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    UUID id = UUID.randomUUID();
    planDTO.setId(id);
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(planDTO);

    Mockito.when(planRepository.findById(id)).thenReturn(Optional.of(planEntity));

    mockMvc.perform(get("/v1/plans/{id}", id))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.planName").value(planDTO.getPlanName()));

  }

  @Test
  void testUpdatePlan() throws Exception {
    UUID planId = UUID.randomUUID();
    PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
    originalPlanDTO.setId(planId);
    originalPlanDTO.setTotalLimit(200);
    originalPlanDTO.setPackageId(UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac2d"));
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(originalPlanDTO);

    Mockito.when(planRepository.findById(eq(planId))).thenReturn(Optional.of(planEntity));
    Mockito.when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);

    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setId(originalPlanDTO.getPackageId());
    packageEntity.setContributions(1000);
    when(packageRepository.findById(originalPlanDTO.getPackageId())).thenReturn(Optional.of(packageEntity));

    mockMvc.perform(put("/v1/plans/{id}", planId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(originalPlanDTO)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.planName").value(originalPlanDTO.getPlanName()))
        .andExpect(jsonPath("$.status").value(originalPlanDTO.getStatus().name()))
        .andExpect(jsonPath("$.planType").value(originalPlanDTO.getPlanType().name()));

  }

  @Test
  void testDeactivatePlan() throws Exception {
    UUID id = UUID.randomUUID();
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    planDTO.setId(id);
    planDTO.setStatus(PlanStatus.DEACTIVATED);
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(planDTO);

    Mockito.when(planRepository.findById(id)).thenReturn(Optional.of(planEntity));
    Mockito.when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);

    mockMvc.perform(delete("/v1/plans/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));

  }

  @Test
  public void testGetFilteredSortedPlans() throws Exception {
    List<PlanDTO> planDTOs = easyRandom.objects(PlanDTO.class, 3).toList();
    List<PlanEntity> planEntities = planDTOs.stream()
        .map(PlanMapper.INSTANCE::toPlanEntity)
        .toList();
    Page<PlanDTO> pagedPlans = new PageImpl<>(planDTOs);

    Mockito.when(planRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(planEntities));

    mockMvc.perform(get("/v1/plans/filtered")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "planName,asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3))
        .andExpect(jsonPath("$.content[0].planName").value(planDTOs.get(0).getPlanName()))
        .andExpect(jsonPath("$.content[1].planName").value(planDTOs.get(1).getPlanName()))
        .andExpect(jsonPath("$.content[2].planName").value(planDTOs.get(2).getPlanName()));

  }

}