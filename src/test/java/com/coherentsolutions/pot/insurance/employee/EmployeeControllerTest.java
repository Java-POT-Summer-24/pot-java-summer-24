package com.coherentsolutions.pot.insurance.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insurance.controller.EmployeeController;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Nested
@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

  private final EasyRandom easyRandom = new EasyRandom();

  @Mock
  private EmployeeService employeeService;

  @InjectMocks
  private EmployeeController employeeController;

  @Test
  void testAddEmployee() {
    EmployeeDTO newEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    when(employeeService.addEmployee(any(EmployeeDTO.class))).thenReturn(newEmployeeDTO);

    EmployeeDTO createdEmployeeDTO = employeeController.addEmployee(newEmployeeDTO);

    assertEquals(newEmployeeDTO, createdEmployeeDTO);
    verify(employeeService).addEmployee(newEmployeeDTO);
  }

  @Test
  void testGetEmployee() {
    EmployeeDTO employeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    UUID id = UUID.randomUUID();
    employeeDTO.setEmployeeId(id);
    when(employeeService.getEmployee(id)).thenReturn(employeeDTO);

    EmployeeDTO result = employeeController.getEmployee(id);

    assertEquals(employeeDTO.getEmployeeId(), result.getEmployeeId());
    verify(employeeService).getEmployee(id);
  }

  @Test
  void testGetAllEmployees() {
    List<EmployeeDTO> employeesList = easyRandom.objects(EmployeeDTO.class, 3).toList();
    when(employeeService.getAllEmployees()).thenReturn(employeesList);

    List<EmployeeDTO> result = employeeController.getAllEmployees();

    assertEquals(3, result.size());
    verify(employeeService).getAllEmployees();
  }

  @Test
  void testUpdateEmployee() {
    EmployeeDTO originalEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    EmployeeDTO updatedEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    updatedEmployeeDTO.setEmployeeId((originalEmployeeDTO.getEmployeeId()));

    when(employeeService.updateEmployee(any(UUID.class), any(EmployeeDTO.class))).thenReturn(
        updatedEmployeeDTO);

    EmployeeDTO result = employeeController.updateEmployee(originalEmployeeDTO.getEmployeeId(),
        originalEmployeeDTO);

    assertEquals(updatedEmployeeDTO, result);
    verify(employeeService).updateEmployee(originalEmployeeDTO.getEmployeeId(),
        originalEmployeeDTO);
  }

  @Test
  void testDeactivateEmployee() {
    UUID id = UUID.randomUUID();
    doNothing().when(employeeService).deleteEmployee(id);

    employeeController.deactivateEmployee(id);

    verify(employeeService).deleteEmployee(id);
  }
}