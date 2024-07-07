package com.coherentsolutions.pot.insurance.plan;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import com.coherentsolutions.pot.insurance.mapper.PlanMapper;
import com.coherentsolutions.pot.insurance.repository.PlanRepository;
import com.coherentsolutions.pot.insurance.specifications.PlanFilterCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.jeasy.random.EasyRandom;
import com.coherentsolutions.pot.insurance.controller.PlanController;
import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.service.PlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PlanController.class)
@ExtendWith(SpringExtension.class)
public class PlanControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PlanRepository planRepository;

  @MockBean
  private PlanService planService;

  private final EasyRandom easyRandom = new EasyRandom();

  @Test
  @WithMockUser(username = "admin")
  void testAddPlan() throws Exception {
    PlanDTO newPlanDTO = easyRandom.nextObject(PlanDTO.class);
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(newPlanDTO);

    when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);
    when(planService.addPlan(any(PlanDTO.class))).thenReturn(newPlanDTO);

    mockMvc.perform(post("/v1/plans")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newPlanDTO)))
        .andExpect(status().isCreated())
        .andExpect(content().json(objectMapper.writeValueAsString(newPlanDTO)))
        .andExpect(jsonPath("$.planName").value(newPlanDTO.getPlanName()));

    verify(planService).addPlan(newPlanDTO);
  }

  @Test
  @WithMockUser(username = "admin")
  void testGetAllPlans() throws Exception {
    List<PlanDTO> planDTOs = easyRandom.objects(PlanDTO.class, 3).toList();
    List<PlanEntity> planEntities = planDTOs.stream()
        .map(PlanMapper.INSTANCE::toPlanEntity)
        .toList();

    when(planRepository.findAll()).thenReturn(planEntities);
    when(planService.getAllPlans()).thenReturn(planDTOs);

    mockMvc.perform(get("/v1/plans")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(content().json(objectMapper.writeValueAsString(planDTOs)));

    verify(planService).getAllPlans();
  }

  @Test
  @WithMockUser(username = "admin")
  void testGetPlanById() throws Exception {
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    UUID id = UUID.randomUUID();
    planDTO.setId(id);
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(planDTO);

    when(planRepository.findById(id)).thenReturn(Optional.of(planEntity));
    when(planService.getPlanById(id)).thenReturn(planDTO);

    mockMvc.perform(get("/v1/plans/{id}", id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(planDTO)));

    verify(planService).getPlanById(id);
  }

  @Test
  @WithMockUser(username = "admin")
  void testUpdatePlan() throws Exception {
    PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
    PlanDTO updatedPlanDTO = easyRandom.nextObject(PlanDTO.class);
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(originalPlanDTO);

    UUID planId = UUID.randomUUID();
    originalPlanDTO.setId(planId);
    updatedPlanDTO.setId(planId);

    when(planRepository.findById(eq(planId))).thenReturn(Optional.of(planEntity));
    when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);
    when(planService.updatePlan(eq(planId), any(PlanDTO.class))).thenReturn(updatedPlanDTO);

    mockMvc.perform(put("/v1/plans/{id}", planId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(originalPlanDTO)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(updatedPlanDTO)))
        .andExpect(jsonPath("$.id").value(planId.toString()))
        .andExpect(jsonPath("$.planName").value(updatedPlanDTO.getPlanName()))
        .andExpect(jsonPath("$.status").value(updatedPlanDTO.getStatus().name()))
        .andExpect(jsonPath("$.planType").value(updatedPlanDTO.getPlanType().name()));

    verify(planService).updatePlan(eq(planId), any(PlanDTO.class));
  }

  @Test
  @WithMockUser(username = "admin")
  void testDeactivatePlan() throws Exception {
    UUID id = UUID.randomUUID();
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    planDTO.setId(id);
    planDTO.setStatus(PlanStatus.DEACTIVATED);
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(planDTO);

    when(planRepository.findById(id)).thenReturn(Optional.of(planEntity));
    when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);
    when(planService.deactivatePlan(id)).thenReturn(planDTO);

    mockMvc.perform(delete("/v1/plans/{id}", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(planDTO)))
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));

    verify(planService).deactivatePlan(id);
  }

  @Test
  @WithMockUser(username = "admin")
  public void testGetFilteredSortedPlans() throws Exception {
    List<PlanDTO> planDTOs = easyRandom.objects(PlanDTO.class, 3).toList();
    Page<PlanDTO> pagedPlans = new PageImpl<>(planDTOs);

    when(planService.getFilteredSortedPlans(any(PlanFilterCriteria.class), any(Pageable.class)))
        .thenReturn(pagedPlans);

    mockMvc.perform(get("/v1/plans/filtered")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "planName,asc")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(pagedPlans)))
        .andExpect(jsonPath("$.content", hasSize(3)))
        .andExpect(jsonPath("$.content[0].id").value(planDTOs.get(0).getId().toString()))
        .andExpect(jsonPath("$.content[0].planName").value(planDTOs.get(0).getPlanName()))
        .andExpect(jsonPath("$.content[1].id").value(planDTOs.get(1).getId().toString()))
        .andExpect(jsonPath("$.content[1].planName").value(planDTOs.get(1).getPlanName()))
        .andExpect(jsonPath("$.content[2].id").value(planDTOs.get(2).getId().toString()))
        .andExpect(jsonPath("$.content[2].planName").value(planDTOs.get(2).getPlanName()));

    verify(planService).getFilteredSortedPlans(any(PlanFilterCriteria.class), any(Pageable.class));
  }

}