package com.coherentsolutions.pot.insurance.claim;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    ResponseEntity<ClaimDTO> response = claimController.addClaim(newClaimDTO);
    ClaimDTO createdClaimDTO = response.getBody();

    assertEquals(newClaimDTO, createdClaimDTO);
    verify(claimService).addClaim(newClaimDTO);
  }

  @Test
  void testGetAllClaims() {
    List<ClaimDTO> claimsList = easyRandom.objects(ClaimDTO.class, 3).toList();
    when(claimService.getAllClaims()).thenReturn(claimsList);

    ResponseEntity<List<ClaimDTO>> response = claimController.getAllClaims();
    List<ClaimDTO> result = response.getBody();

    assertEquals(3, result.size());
    verify(claimService).getAllClaims();
  }

  @Test
  void testGetClaimById() {
    ClaimDTO claimDTO = easyRandom.nextObject(ClaimDTO.class);
    UUID id = UUID.randomUUID();
    claimDTO.setId(id);
    when(claimService.getClaimById(id)).thenReturn(claimDTO);

    ResponseEntity<ClaimDTO> response = claimController.getClaimById(id);
    ClaimDTO result = response.getBody();

    assertEquals(claimDTO.getId(), result.getId());
    verify(claimService).getClaimById(id);
  }

  @Test
  void testUpdateClaim() {
    ClaimDTO originalClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    UUID id = UUID.randomUUID();
    originalClaimDTO.setId(id);
    ClaimDTO updatedClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    updatedClaimDTO.setId(id);

    when(claimService.updateClaim(any(ClaimDTO.class))).thenReturn(updatedClaimDTO);

    ResponseEntity<ClaimDTO> response = claimController.updateClaim(id, originalClaimDTO);
    ClaimDTO result = response.getBody();

    assertEquals(updatedClaimDTO, result);
    verify(claimService).updateClaim(originalClaimDTO);
  }

  @Test
  void testDeleteClaim() {
    UUID id = UUID.randomUUID();
    doNothing().when(claimService).deleteClaim(id);

    ResponseEntity<Void> response = claimController.deleteClaim(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(claimService).deleteClaim(id);
  }
}
