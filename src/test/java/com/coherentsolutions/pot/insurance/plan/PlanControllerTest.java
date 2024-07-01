package com.coherentsolutions.pot.insurance.plan;


import com.coherentsolutions.pot.insurance.controller.PlanController;
import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.dto.enums.PlanStatus;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.service.PlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.jeasy.random.EasyRandom;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanControllerTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Mock
    private PlanService planService;

    @InjectMocks
    private PlanController planController;

    @Test
    void testAddPlan() {
        PlanDTO newPlanDTO = easyRandom.nextObject(PlanDTO.class);

        when(planService.addPlan(any(PlanDTO.class))).thenReturn(newPlanDTO);

        PlanDTO createdPlanDTO = planController.addPlan(newPlanDTO);

        assertEquals(newPlanDTO, createdPlanDTO);
        verify(planService).addPlan(newPlanDTO);
    }

    @Test
    void testGetAllPlans() {
        List<PlanDTO> plansList = easyRandom.objects(PlanDTO.class, 3).toList();
        when(planService.getAllPlans()).thenReturn(plansList);

        List<PlanDTO> result = planController.getAllPlans();

        assert result != null;
        assertEquals(3, result.size());
        verify(planService).getAllPlans();
    }

    @Test
    void testGetPlanById() {
        PlanDTO PlanDTO = easyRandom.nextObject(PlanDTO.class);
        UUID id = UUID.randomUUID();
        PlanDTO.setPlanId(id);
        when(planService.getPlanById(id)).thenReturn(PlanDTO);

        PlanDTO result = planController.getPlanById(id);

        assert result != null;
        assertEquals(PlanDTO.getPlanId(), result.getPlanId());
        verify(planService).getPlanById(id);
    }

    @Test
    void testUpdatePlan() {
        PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
        PlanDTO updatedPlanDTO = easyRandom.nextObject(PlanDTO.class);
        updatedPlanDTO.setPlanId(originalPlanDTO.getPlanId());

        when(planService.updatePlan(any(PlanDTO.class))).thenReturn(updatedPlanDTO);

        PlanDTO result = planController.updatePlan(originalPlanDTO);

        assertEquals(updatedPlanDTO, result);
        verify(planService).updatePlan(originalPlanDTO);
    }

    @Test
    void testDeactivatePlan() {
        UUID id = UUID.randomUUID();
        PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
        originalPlanDTO.setPlanId(id);
        originalPlanDTO.setStatus(PlanStatus.DEACTIVATED);

        when(planService.deactivatePlan(id)).thenReturn(originalPlanDTO);

        PlanDTO resultPlanDTO = planController.deactivatePlan(id);
        
        assert resultPlanDTO != null;
        assertEquals(PlanStatus.DEACTIVATED, resultPlanDTO.getStatus());

        verify(planService).deactivatePlan(id);
    }

    @Test
    void testDeactivateNonExistingPlan() {
        UUID nonExistentId = UUID.randomUUID();

        // Simulate NotFoundException being thrown by planService.deactivatePlan(id)
        when(planService.deactivatePlan(nonExistentId)).thenThrow(NotFoundException.class);

        try {
            planController.deactivatePlan(nonExistentId);
        } catch (NotFoundException e) {
            // NotFoundException expected, test passes
            verify(planService).deactivatePlan(nonExistentId);
            return;
        }
        // If NotFoundException is not thrown, fail the test
        throw new AssertionError("Expected NotFoundException was not thrown");
    }
}

