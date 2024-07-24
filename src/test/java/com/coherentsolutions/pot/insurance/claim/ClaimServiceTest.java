package com.coherentsolutions.pot.insurance.claim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.mapper.ClaimMapper;
import com.coherentsolutions.pot.insurance.repository.ClaimRepository;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.specifications.ClaimFilterCriteria;
import com.coherentsolutions.pot.insurance.util.NotificationClient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

  private final EasyRandom easyRandom = new EasyRandom();

  @Mock
  private ClaimRepository claimRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private CompanyRepository companyRepository;

  @Mock
  private NotificationClient notificationClient;
  @InjectMocks
  private ClaimService claimService;

  @Test
  void testAddClaim() {
    ClaimDTO newClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(newClaimDTO);
    claimEntity.setId(UUID.randomUUID());
    EmployeeEntity employeeEntity = new EmployeeEntity();
    employeeEntity.setUserName(newClaimDTO.getEmployeeUserName());
    CompanyEntity companyEntity = new CompanyEntity();
    companyEntity.setName(newClaimDTO.getCompany());

    when(employeeRepository.findByUserName(newClaimDTO.getEmployeeUserName())).thenReturn(Optional.of(employeeEntity));
    when(companyRepository.findByName(newClaimDTO.getCompany())).thenReturn(Optional.of(companyEntity));
    when(claimRepository.save(any(ClaimEntity.class))).thenReturn(claimEntity);

    ClaimDTO createdClaimDTO = claimService.addClaim(newClaimDTO);

    assertEquals(claimEntity.getId(), createdClaimDTO.getId());
    assertEquals(newClaimDTO.getAmount(), createdClaimDTO.getAmount());
    verify(claimRepository).save(any(ClaimEntity.class));
  }

  @Test
  void testGetAllClaims() {
    List<ClaimEntity> claimEntities = easyRandom.objects(ClaimEntity.class, 3).toList();
    when(claimRepository.findAll()).thenReturn(claimEntities);

    List<ClaimDTO> result = claimService.getAllClaims();

    assertEquals(claimEntities.size(), result.size());
    verify(claimRepository).findAll();
  }

  @Test
  void testGetClaimById() {
    UUID id = UUID.randomUUID();
    ClaimEntity claimEntity = easyRandom.nextObject(ClaimEntity.class);
    claimEntity.setId(id);
    when(claimRepository.findById(id)).thenReturn(java.util.Optional.of(claimEntity));

    ClaimDTO result = claimService.getClaimById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(claimRepository).findById(id);
  }

  @Test
  void testUpdateClaim() {
    UUID claimId = UUID.randomUUID();
    ClaimDTO originalClaimDTO = easyRandom.nextObject(ClaimDTO.class);
    originalClaimDTO.setId(claimId);
    ClaimEntity claimEntity = ClaimMapper.INSTANCE.dtoToEntity(originalClaimDTO);

    when(claimRepository.findById(claimId)).thenReturn(java.util.Optional.of(claimEntity));
    when(claimRepository.save(any(ClaimEntity.class))).thenReturn(claimEntity);

    ClaimDTO updatedClaimDTO = claimService.updateClaim(claimId, originalClaimDTO);

    assertEquals(originalClaimDTO.getAmount(), updatedClaimDTO.getAmount());
    verify(claimRepository).save(claimEntity);
  }

  @Test
  void testDeactivateClaim() {
    UUID id = UUID.randomUUID();
    ClaimEntity claimEntity = easyRandom.nextObject(ClaimEntity.class);
    claimEntity.setId(id);
    claimEntity.setStatus(ClaimStatus.APPROVED);

    when(claimRepository.findById(id)).thenReturn(java.util.Optional.of(claimEntity));
    when(claimRepository.save(any(ClaimEntity.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    ClaimDTO result = claimService.deactivateClaim(id);

    assertEquals(ClaimStatus.DEACTIVATED, result.getStatus());
    verify(claimRepository).save(claimEntity);
  }

  @Test
  void testGetAllClaimsByEmployeeUserName() {
    List<ClaimEntity> claimEntities = easyRandom.objects(ClaimEntity.class, 3).toList();
    List<ClaimDTO> claimsList = claimEntities.stream().map(ClaimMapper.INSTANCE::entityToDto).toList();
    String employee = "janedoe";

    when(claimRepository.findAllByEmployeeUserName(employee)).thenReturn(claimEntities);

    List<ClaimDTO> result = claimService.getAllClaimsByEmployeeUserName(employee);

    assertEquals(3, result.size());
    assertEquals(claimsList, result);
    verify(claimRepository).findAllByEmployeeUserName(employee);
  }

  @Test
  void testGetAllClaimsByCompanyName() {
    List<ClaimEntity> claimEntities = easyRandom.objects(ClaimEntity.class, 3).toList();
    List<ClaimDTO> claimsList = claimEntities.stream().map(ClaimMapper.INSTANCE::entityToDto).toList();
    String company = "ISSoft";

    when(claimRepository.findAllByCompanyName(company)).thenReturn(claimEntities);

    List<ClaimDTO> result = claimService.getAllClaimsByCompanyName(company);

    assertEquals(3, result.size());
    assertEquals(claimsList, result);
    verify(claimRepository).findAllByCompanyName(company);
  }

  @Test
  void testGetFilteredSortedClaims() {
    List<ClaimEntity> claimEntities = easyRandom.objects(ClaimEntity.class, 3).toList();
    Page<ClaimEntity> pagedEntities = new PageImpl<>(claimEntities);

    ClaimFilterCriteria filterCriteria = new ClaimFilterCriteria();
    Pageable pageable = PageRequest.of(0, 3, Sort.by("dateOfService").descending());

    when(claimRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagedEntities);

    Page<ClaimDTO> result = claimService.getFilteredSortedClaims(filterCriteria, pageable);

    assertNotNull(result);
    assertEquals(3, result.getContent().size());
    assertEquals(claimEntities.stream().map(ClaimMapper.INSTANCE::entityToDto).toList(), result.getContent());

    verify(claimRepository).findAll(any(Specification.class), any(Pageable.class));
  }
}
