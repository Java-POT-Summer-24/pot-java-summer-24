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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
  void testGetFilteredSortedClaims() {
    List<ClaimDTO> claimsList = easyRandom.objects(ClaimDTO.class, 3).toList();
    Page<ClaimDTO> pagedClaims = new PageImpl<>(claimsList);

    int page = 0;
    int size = 10;
    String sort = "dateOfService,desc";
    Sort sortedBy = Sort.by(Sort.Order.desc(sort.split(",")[0]));
    PageRequest pageable = PageRequest.of(page, size, sortedBy);

    when(claimService.getFilteredSortedClaims(null, null, null, null, page, size, sortedBy))
        .thenReturn(pagedClaims);

    ResponseEntity<Page<ClaimDTO>> response = claimController.getFilteredSortedClaims(null, null, null, null, page, size, sort);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(3, response.getBody().getContent().size());
    assertEquals(claimsList, response.getBody().getContent());

    verify(claimService).getFilteredSortedClaims(null, null, null, null, page, size, sortedBy);
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
    ClaimDTO updatedClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    updatedClaimDTO.setId(originalClaimDTO.getId());

    when(claimService.updateClaim(any(ClaimDTO.class))).thenReturn(updatedClaimDTO);

    ResponseEntity<ClaimDTO> response = claimController.updateClaim(originalClaimDTO);
    ClaimDTO result = response.getBody();

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

    ResponseEntity<ClaimDTO> response = claimController.deactivateClaim(id);
    ClaimDTO resultClaimDTO = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(ClaimStatus.DEACTIVATED, resultClaimDTO.getStatus());

    verify(claimService).deactivateClaim(id);
  }
}