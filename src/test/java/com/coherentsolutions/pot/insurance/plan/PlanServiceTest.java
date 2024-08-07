package com.coherentsolutions.pot.insurance.plan;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.PlanMapper;
import com.coherentsolutions.pot.insurance.repository.PackageRepository;
import com.coherentsolutions.pot.insurance.repository.PlanRepository;
import com.coherentsolutions.pot.insurance.service.PlanService;
import com.coherentsolutions.pot.insurance.specifications.PlanFilterCriteria;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jeasy.random.EasyRandom;
import java.util.List;
import java.util.UUID;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

  private final EasyRandom easyRandom = new EasyRandom();

  @InjectMocks
  private PlanService planService;
  @Mock
  private PlanRepository planRepository;
  @Mock
  private PackageRepository packageRepository;

  @Test
  void testAddPlan() {
    PlanDTO newPlanDTO = easyRandom.nextObject(PlanDTO.class);
    newPlanDTO.setPackageId(UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac2d"));
    newPlanDTO.setTotalLimit(100);
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(newPlanDTO);

    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setId(newPlanDTO.getPackageId());
    packageEntity.setContributions(1000);
    when(packageRepository.findById(newPlanDTO.getPackageId())).thenReturn(Optional.of(packageEntity));

    when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);
    PlanDTO createdPlanDTO = planService.addPlan(newPlanDTO);

    assertEquals(planEntity.getId(), createdPlanDTO.getId());
    assertEquals(newPlanDTO.getPlanName(), createdPlanDTO.getPlanName());
    verify(planRepository).save(any(PlanEntity.class));
  }

  @Test
  void testGetAllPlans() {
    List<PlanEntity> planEntities = easyRandom.objects(PlanEntity.class, 3).toList();

    when(planRepository.findAll()).thenReturn(planEntities);

    List<PlanDTO> result = planService.getAllPlans();

    assertEquals(planEntities.size(), result.size());
    verify(planRepository).findAll();
  }

  @Test
  void testGetPlanById() {
    PlanEntity planEntity = easyRandom.nextObject(PlanEntity.class);
    UUID id = planEntity.getId();
    when(planRepository.findById(id)).thenReturn(Optional.of(planEntity));

    PlanDTO result = planService.getPlanById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(planRepository).findById(id);
  }

  @Test
  void testUpdatePlan() {
    PlanDTO originalPlanDTO = easyRandom.nextObject(PlanDTO.class);
    originalPlanDTO.setTotalLimit(200);
    originalPlanDTO.setPackageId(UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac2d"));
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(originalPlanDTO);
    UUID planId = originalPlanDTO.getId();

    when(planRepository.findById(planId)).thenReturn(Optional.of(planEntity));
    when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);

    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setId(originalPlanDTO.getPackageId());
    packageEntity.setContributions(1000);
    when(packageRepository.findById(originalPlanDTO.getPackageId())).thenReturn(Optional.of(packageEntity));

    PlanDTO updatedPlanDTO = planService.updatePlan(planId, originalPlanDTO);

    assertEquals(originalPlanDTO.getPlanName(), updatedPlanDTO.getPlanName());
    verify(planRepository).save(planEntity);
  }

  @Test
  void testDeactivatePlan() {
    PlanEntity planEntity = easyRandom.nextObject(PlanEntity.class);
    planEntity.setStatus(PlanStatus.ACTIVE);
    UUID id = planEntity.getId();

    when(planRepository.findById(id)).thenReturn(Optional.of(planEntity));
    when(planRepository.save(any(PlanEntity.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    PlanDTO result = planService.deactivatePlan(id);

    assertEquals(PlanStatus.DEACTIVATED, result.getStatus());
    verify(planRepository).save(planEntity);
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
  public void testGetFilteredSortedPlans() {
    List<PlanEntity> planEntities = easyRandom.objects(PlanEntity.class, 3).toList();
    Page<PlanEntity> pagedEntities = new PageImpl<>(planEntities);

    PlanFilterCriteria filterCriteria = new PlanFilterCriteria();
    Pageable pageable = PageRequest.of(0, 3, Sort.by("planName").ascending());

    when(planRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(
        pagedEntities);

    Page<PlanDTO> result = planService.getFilteredSortedPlans(filterCriteria, pageable);

    assertNotNull(result);
    assertEquals(3, result.getContent().size());
    assertEquals(planEntities.stream().map(PlanMapper.INSTANCE::toPlanDto).toList(),
        result.getContent());

    verify(planRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void testAddPlanValidContribution() {
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    planDTO.setTotalLimit(100);
    planDTO.setPackageId(UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac2d"));
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(planDTO);

    UUID packageId = planDTO.getPackageId();
    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setId(packageId);
    packageEntity.setContributions(1000);

    when(planRepository.findSumOfTotalLimitByPackageId(packageId, PlanStatus.ACTIVE)).thenReturn(500.0);
    when(packageRepository.findById(packageId)).thenReturn(Optional.of(packageEntity));
    when(planRepository.save(any(PlanEntity.class))).thenReturn(planEntity);

    PlanDTO result = planService.addPlan(planDTO);

    assertNotNull(result);
    assertEquals(planDTO.getPlanName(), result.getPlanName());
    assertEquals(planDTO.getTotalLimit(), result.getTotalLimit());
  }

  @Test
  void testAddPlanInvalidContribution() {
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    planDTO.setTotalLimit(600);
    planDTO.setPackageId(UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac2d"));
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(planDTO);

    UUID packageId = planDTO.getPackageId();
    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setId(packageId);
    packageEntity.setContributions(1000);

    when(planRepository.findSumOfTotalLimitByPackageId(packageId, PlanStatus.ACTIVE)).thenReturn(500.0);
    when(packageRepository.findById(packageId)).thenReturn(Optional.of(packageEntity));

    assertThrows(IllegalStateException.class, () -> planService.addPlan(planDTO));
  }

  @Test
  void testAddPlanPackageNotFound() {
    PlanDTO planDTO = easyRandom.nextObject(PlanDTO.class);
    planDTO.setPackageId(UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac2d"));
    PlanEntity planEntity = PlanMapper.INSTANCE.toPlanEntity(planDTO);

    UUID packageId = planDTO.getPackageId();

    when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> planService.addPlan(planDTO));
  }
}
