package com.coherentsolutions.pot.insurance.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.mapper.EmployeeMapper;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import com.coherentsolutions.pot.insurance.specifications.EmployeeFilterCriteria;
import com.coherentsolutions.pot.insurance.util.NotificationClient;
import java.util.List;
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
class EmployeeServiceTest {
  private final EasyRandom easyRandom = new EasyRandom();
  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private NotificationClient notificationClient;

  @InjectMocks
  private EmployeeService employeeService;

  @Test
  void testGetFilteredSortedEmployees() {
    List<EmployeeEntity> employeeEntities = easyRandom.objects(EmployeeEntity.class, 3).toList();
    Page<EmployeeEntity> pagedEntities = new PageImpl<>(employeeEntities);

    EmployeeFilterCriteria filterCriteria = new EmployeeFilterCriteria();
    Pageable pageable = PageRequest.of(0, 3, Sort.by("dateOfService").descending());

    when(employeeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagedEntities);

    Page<EmployeeDTO> result = employeeService.getFilteredSortedEmployees(filterCriteria, pageable);

    assertNotNull(result);
    assertEquals(3, result.getContent().size());
    assertEquals(employeeEntities.stream().map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO).toList(), result.getContent());

    verify(employeeRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void testAddEmployee() {
    EmployeeDTO newEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    EmployeeEntity employeeEntity = new EmployeeEntity();

    when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(employeeEntity);

    EmployeeDTO createdEmployeeDTO = employeeService.addEmployee(newEmployeeDTO);

    verify(employeeRepository).save(any(EmployeeEntity.class));
  }

  @Test
  void testGetAllEmployees() {
    List<EmployeeEntity> employeeEntities = easyRandom.objects(EmployeeEntity.class, 3).toList();
    when(employeeRepository.findAll()).thenReturn(employeeEntities);

    List<EmployeeDTO> result = employeeService.getAllEmployees();

    assertEquals(employeeEntities.size(), result.size());
    verify(employeeRepository).findAll();
  }

  @Test
  void testGetEmployeeById() {
    UUID id = UUID.randomUUID();
    EmployeeEntity employeeEntity = easyRandom.nextObject(EmployeeEntity.class);
    employeeEntity.setId(id);
    when(employeeRepository.findById(id)).thenReturn(java.util.Optional.of(employeeEntity));

    EmployeeDTO result = employeeService.getEmployee(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(employeeRepository).findById(id);
  }

  @Test
  void testUpdateEmployee() {
    UUID employeeId = UUID.randomUUID();
    EmployeeDTO originalEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    originalEmployeeDTO.setId(employeeId);
    EmployeeEntity employeeEntity = EmployeeMapper.INSTANCE.employeeDTOToEmployee(originalEmployeeDTO);

    when(employeeRepository.findById(employeeId)).thenReturn(java.util.Optional.of(employeeEntity));
    when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(employeeEntity);

    EmployeeDTO updatedEmployeeDTO = employeeService.updateEmployee(employeeId, originalEmployeeDTO);

    assertEquals(originalEmployeeDTO.getFirstName(), updatedEmployeeDTO.getFirstName());
    verify(employeeRepository).save(employeeEntity);
  }

  @Test
  void testDeactivateEmployee() {
    UUID id = UUID.randomUUID();
    EmployeeEntity employeeEntity = easyRandom.nextObject(EmployeeEntity.class);
    employeeEntity.setId(id);
    employeeEntity.setStatus(EmployeeStatus.ACTIVE);

    when(employeeRepository.findById(id)).thenReturn(java.util.Optional.of(employeeEntity));
    when(employeeRepository.save(any(EmployeeEntity.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    EmployeeDTO result = employeeService.deactivateEmployee(id);

    assertEquals(EmployeeStatus.INACTIVE, result.getStatus());
    verify(employeeRepository).save(employeeEntity);
  }
}
