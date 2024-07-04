package com.coherentsolutions.pot.insurance.plan;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.specifications.PlanFilterCriteria;
import java.util.List;
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
  private PlanService planService;

  private final EasyRandom easyRandom = new EasyRandom();

  @Test
  @WithMockUser(username = "admin")
  void testAddPlan() throws Exception {
    PlanDTO newPlanDTO = easyRandom.nextObject(PlanDTO.class);

    when(planService.addPlan(any(PlanDTO.class))).thenReturn(newPlanDTO);

    mockMvc.perform(post("/v1/plans")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newPlanDTO)))
        .andExpect(status().isCreated())
        .andExpect(content().json(objectMapper.writeValueAsString(newPlanDTO)));
    verify(planService).addPlan(newPlanDTO);
  }

  @Test
  @WithMockUser(username = "admin")
  void testGetAllPlans() throws Exception {
    List<PlanDTO> plansList = easyRandom.objects(PlanDTO.class, 3).toList();
    when(planService.getAllPlans()).thenReturn(plansList);

    mockMvc.perform(get("/v1/plans")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(plansList)));

    verify(planService).getAllPlans();
  }

  @Test
  @WithMockUser(username = "admin")
  void testGetPlanById() throws Exception {
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    UUID id = UUID.randomUUID();
    planDTO.setId(id);
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

    UUID planId = UUID.randomUUID();
    originalPlanDTO.setId(planId);
    updatedPlanDTO.setId(planId);

    when(planService.updatePlan(eq(planId), any(PlanDTO.class))).thenReturn(updatedPlanDTO);

    mockMvc.perform(put("/v1/plans/{id}", planId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(originalPlanDTO)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(updatedPlanDTO)));

    verify(planService).updatePlan(eq(planId), any(PlanDTO.class));
  }

  @Test
  @WithMockUser(username = "admin")
  void testDeactivatePlan() throws Exception {
    UUID id = UUID.randomUUID();
    PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
    originalPlanDTO.setId(id);

    when(planService.deactivatePlan(id)).thenReturn(originalPlanDTO);

    mockMvc.perform(delete("/v1/plans/{id}", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(originalPlanDTO)));

    verify(planService).deactivatePlan(id);
  }

  @Test
  @WithMockUser(username = "admin")
  public void testGetFilteredSortedPlans() throws Exception {
    List<PlanDTO> plansList = easyRandom.objects(PlanDTO.class, 3).toList();
    Page<PlanDTO> pagedPlans = new PageImpl<>(plansList);
    when(planService.getFilteredSortedPlans(any(PlanFilterCriteria.class), any(Pageable.class)))
        .thenReturn(pagedPlans);

    mockMvc.perform(get("/v1/plans/filtered")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "planName,asc")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(pagedPlans)));
  }

}