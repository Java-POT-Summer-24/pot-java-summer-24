package com.coherentsolutions.pot.insurance.claim;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.controller.ClaimController;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
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
class ClaimControllerTest {

  private final EasyRandom easyRandom = new EasyRandom();

  @Mock
  private ClaimService claimService;

  @InjectMocks
  private ClaimController claimController;

  @Test
  void testAddClaim() {
    ClaimDTO newClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    when(claimService.addClaim(any(ClaimDTO.class))).thenReturn(newClaimDTO);

    ClaimDTO createdClaimDTO = claimController.addClaim(newClaimDTO);

    assertEquals(newClaimDTO, createdClaimDTO);
    verify(claimService).addClaim(newClaimDTO);
  }

  @Test
  void testGetAllClaims() {
    List<ClaimDTO> claimsList = easyRandom.objects(ClaimDTO.class, 3).toList();
    when(claimService.getAllClaims()).thenReturn(claimsList);

    List<ClaimDTO> result = claimController.getAllClaims();

    assertEquals(3, result.size());
    verify(claimService).getAllClaims();
  }

  @Test
  void testGetClaimById() {
    ClaimDTO claimDTO = easyRandom.nextObject(ClaimDTO.class);
    UUID id = UUID.randomUUID();
    claimDTO.setId(id);
    when(claimService.getClaimById(id)).thenReturn(claimDTO);

    ClaimDTO result = claimController.getClaimById(id);

    assertEquals(claimDTO.getId(), result.getId());
    verify(claimService).getClaimById(id);
  }

  @Test
  void testUpdateClaim() {
    UUID claimId = UUID.randomUUID();
    ClaimDTO originalClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    ClaimDTO updatedClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    updatedClaimDTO.setId(claimId);

    when(claimService.updateClaim(eq(claimId), any(ClaimDTO.class))).thenReturn(updatedClaimDTO);

    ClaimDTO result = claimController.updateClaim(claimId, originalClaimDTO);

    assertEquals(updatedClaimDTO, result);
    verify(claimService).updateClaim(eq(claimId), any(ClaimDTO.class));
  }

  @Test
  void testDeactivateClaim() {
    UUID id = UUID.randomUUID();
    ClaimDTO originalClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    originalClaimDTO.setId(id);
    originalClaimDTO.setStatus(ClaimStatus.DEACTIVATED);

    when(claimService.deactivateClaim(id)).thenReturn(originalClaimDTO);

    ClaimDTO resultClaimDTO = claimController.deactivateClaim(id);

    assertEquals(ClaimStatus.DEACTIVATED, resultClaimDTO.getStatus());
    verify(claimService).deactivateClaim(id);
  }

  @Test
  void testGetAllClaimsByEmployee() {
    List<ClaimDTO> claimsList = easyRandom.objects(ClaimDTO.class, 3).toList();
    String employee = "janedoe";
    when(claimService.getAllClaimsByEmployee(employee)).thenReturn(claimsList);

    List<ClaimDTO> result = claimController.getAllClaimsByEmployee(employee);

    assertEquals(3, result.size());
    verify(claimService).getAllClaimsByEmployee(employee);
  }

  @Test
  void testGetAllClaimsByCompany() {
    List<ClaimDTO> claimsList = easyRandom.objects(ClaimDTO.class, 3).toList();
    String company = "ISSoft";
    when(claimService.getAllClaimsByCompany(company)).thenReturn(claimsList);

    List<ClaimDTO> result = claimController.getAllClaimsByCompany(company);

    assertEquals(3, result.size());
    verify(claimService).getAllClaimsByCompany(company);
  }
}