package com.coherentsolutions.pot.insurance.plan;


import com.coherentsolutions.pot.insurance.controller.PlanController;
import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.dto.enums.PlanStatus;
import com.coherentsolutions.pot.insurance.service.PlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.jeasy.random.EasyRandom;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        ResponseEntity<PlanDTO> responseEntity = new ResponseEntity<>(newPlanDTO, HttpStatus.CREATED);

        when(planService.addPlan(any(PlanDTO.class))).thenReturn(responseEntity);

        ResponseEntity<PlanDTO> response = planController.addPlan(newPlanDTO);
        PlanDTO createdPlanDTO = response.getBody();

        assertEquals(newPlanDTO, createdPlanDTO);
        verify(planService).addPlan(newPlanDTO);
    }

    @Test
    void testGetAllPlans() {
        List<PlanDTO> plansList = easyRandom.objects(PlanDTO.class, 3).toList();
        ResponseEntity<List<PlanDTO>> responseEntity = new ResponseEntity<>(plansList, HttpStatus.OK);
        when(planService.getAllPlans()).thenReturn(responseEntity);

        ResponseEntity<List<PlanDTO>> response = planController.getAllPlans();
        List<PlanDTO> result = response.getBody();

        assert result != null;
        assertEquals(3, result.size());
        verify(planService).getAllPlans();
    }

    @Test
    void testGetPlanById() {
        PlanDTO PlanDTO = easyRandom.nextObject(PlanDTO.class);
        ResponseEntity<PlanDTO> responseEntity = new ResponseEntity<>(PlanDTO, HttpStatus.CREATED);
        UUID id = UUID.randomUUID();
        PlanDTO.setPlanId(id);
        when(planService.getPlanById(id)).thenReturn(responseEntity);

        ResponseEntity<PlanDTO> response = planController.getPlanById(id);
        PlanDTO result = response.getBody();

        assert result != null;
        assertEquals(PlanDTO.getPlanId(), result.getPlanId());
        verify(planService).getPlanById(id);
    }

    @Test
    void testUpdatePlan() {
        PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
        PlanDTO updatedPlanDTO = easyRandom.nextObject(PlanDTO.class);
        updatedPlanDTO.setPlanId(originalPlanDTO.getPlanId());
        ResponseEntity<PlanDTO> responseEntity = new ResponseEntity<>(updatedPlanDTO, HttpStatus.CREATED);
        when(planService.updatePlan(any(PlanDTO.class))).thenReturn(responseEntity);

        ResponseEntity<PlanDTO> response = planController.updatePlan(originalPlanDTO);
        PlanDTO result = response.getBody();

        assertEquals(updatedPlanDTO, result);
        verify(planService).updatePlan(originalPlanDTO);
    }

    @Test
    void testDeactivatePlan() {
        UUID id = UUID.randomUUID();
        PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
        originalPlanDTO.setPlanId(id);
        originalPlanDTO.setStatus(PlanStatus.DEACTIVATED);
        ResponseEntity<PlanDTO> responseEntity = new ResponseEntity<>(originalPlanDTO, HttpStatus.CREATED);

        when(planService.deactivatePlan(id)).thenReturn(responseEntity);

        ResponseEntity<PlanDTO> response = planController.deactivatePlan(id);
        PlanDTO resultPlanDTO = response.getBody();

        
        assert resultPlanDTO != null;
        assertEquals(PlanStatus.DEACTIVATED, resultPlanDTO.getStatus());

        verify(planService).deactivatePlan(id);
    }
}

