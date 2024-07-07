package com.coherentsolutions.pot.insurance.plan;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.repository.PlanRepository;
import com.coherentsolutions.pot.insurance.service.PlanService;
import com.coherentsolutions.pot.insurance.specifications.PlanFilterCriteria;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jeasy.random.EasyRandom;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackages = "com.coherentsolutions.pot.insurance")
@Transactional
class PlanServiceTest {

  private final EasyRandom easyRandom = new EasyRandom();

  @Autowired
  private PlanService planService;
  @Autowired
  private PlanRepository planRepository;

  @Test
  void testAddPlan() {
    PlanDTO newPlanDTO = easyRandom.nextObject(PlanDTO.class);
    PlanDTO createdPlanDTO = planService.addPlan(newPlanDTO);

    assertNotNull(createdPlanDTO);
    assertEquals(newPlanDTO.getPlanName(), createdPlanDTO.getPlanName());

    PlanEntity createdPlanEntity = planRepository.findById(createdPlanDTO.getId()).orElse(null);
    assertNotNull(createdPlanEntity);
    assertEquals(newPlanDTO.getPlanName(), createdPlanEntity.getPlanName());
  }

  @Test
  void testGetAllPlans() {
    long repoSize = planRepository.count();

    List<PlanDTO> plansList = easyRandom.objects(PlanDTO.class, 3).toList();
    plansList.forEach(planDTO -> planService.addPlan(planDTO));

    List<PlanDTO> result = planService.getAllPlans();

    assertNotNull(result);
    assertEquals(repoSize + 3, result.size());
  }

  @Test
  void testGetPlanById() {
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    PlanDTO savedPlanDTO = planService.addPlan(planDTO);
    UUID id = savedPlanDTO.getId();

    PlanDTO result = planService.getPlanById(id);

    assertNotNull(result);
    assertEquals(savedPlanDTO.getId(), result.getId());
  }

  @Test
  void testUpdatePlan() {
    PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
    PlanDTO savedPlanDTO = planService.addPlan(originalPlanDTO);

    PlanDTO updatedPlanDTO = easyRandom.nextObject(PlanDTO.class);
    updatedPlanDTO.setId(savedPlanDTO.getId());

    PlanDTO result = planService.updatePlan(savedPlanDTO.getId(), updatedPlanDTO);

    assertNotNull(result);
    assertEquals(updatedPlanDTO.getPlanName(), result.getPlanName());
  }

  @Test
  void testDeactivatePlan() {
    PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
    PlanDTO savedPlanDTO = planService.addPlan(originalPlanDTO);

    PlanDTO resultPlanDTO = planService.deactivatePlan(savedPlanDTO.getId());

    assertNotNull(resultPlanDTO);
    assertEquals(PlanStatus.DEACTIVATED, resultPlanDTO.getStatus());
  }

  @Test
  void testDeactivateNonExistingPlan() {
    UUID id = UUID.randomUUID();

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      planService.deactivatePlan(id);
    });

    assertEquals("Plan with id " + id + " not found", exception.getMessage());
  }

  @Test
  public void testGetFilteredSortedPlans() throws Exception {
    long repoSize = planRepository.count();

    List<PlanDTO> plansList = easyRandom.objects(PlanDTO.class, 3).toList();
    plansList.forEach(planDTO -> planService.addPlan(planDTO));

    PlanFilterCriteria planFilterCriteria = new PlanFilterCriteria();
    Pageable pageable = PageRequest.of(0, 10, Sort.by("planName").ascending());

    Page<PlanDTO> result = planService.getFilteredSortedPlans(planFilterCriteria, pageable);

    assertNotNull(result);
    assertEquals(repoSize + plansList.size(), result.getContent().size());
  }
}
