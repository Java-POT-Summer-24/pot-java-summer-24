package com.coherentsolutions.pot.insurance.claim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.controller.ClaimController;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.specifications.ClaimFilterCriteria;
import java.util.List;
import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ClaimControllerTest {

  private final EasyRandom easyRandom = new EasyRandom();

  @Mock
  private ClaimService claimService;

  @InjectMocks
  private ClaimController claimController;

  @Test
  void testGetAllClaims() {
    List<ClaimDTO> claimsList = easyRandom.objects(ClaimDTO.class, 3).toList();
    when(claimService.getAllClaims()).thenReturn(claimsList);

    List<ClaimDTO> result = claimController.getAllClaims();

    assertEquals(3, result.size());
    verify(claimService).getAllClaims();
  }

  @Test
  void testAddClaim() {
    ClaimDTO newClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    when(claimService.addClaim(any(ClaimDTO.class))).thenReturn(newClaimDTO);

    ClaimDTO createdClaimDTO = claimController.addClaim(newClaimDTO);

    assertEquals(newClaimDTO, createdClaimDTO);
    verify(claimService).addClaim(newClaimDTO);
  }

  @Test
  void testGetFilteredSortedClaims() {
    List<ClaimDTO> claimsList = easyRandom.objects(ClaimDTO.class, 3).toList();
    Page<ClaimDTO> pagedClaims = new PageImpl<>(claimsList);
    ClaimFilterCriteria claimFilterCriteria = new ClaimFilterCriteria();
    Pageable pageable = PageRequest.of(0, 10, Sort.by("dateOfService").descending());

    when(claimService.getFilteredSortedClaims(any(ClaimFilterCriteria.class), any(Pageable.class)))
        .thenReturn(pagedClaims);

    Page<ClaimDTO> result = claimController.getFilteredSortedClaims(claimFilterCriteria, pageable);

    assertNotNull(result);
    assertEquals(3, result.getContent().size());
    assertEquals(claimsList, result.getContent());

    verify(claimService).getFilteredSortedClaims(claimFilterCriteria, pageable);
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
    ClaimDTO originalClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    ClaimDTO updatedClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    updatedClaimDTO.setId(originalClaimDTO.getId());

    when(claimService.updateClaim(any(ClaimDTO.class))).thenReturn(updatedClaimDTO);

    ClaimDTO result = claimController.updateClaim(originalClaimDTO);

    assertEquals(updatedClaimDTO, result);
    verify(claimService).updateClaim(originalClaimDTO);
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
}