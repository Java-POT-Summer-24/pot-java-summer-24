package com.coherentsolutions.pot.insurance.employee;

import com.coherentsolutions.pot.insurance.controller.EmployeeController;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmployeeService employeeService;

  private final EasyRandom easyRandom = new EasyRandom();

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser(username = "admin")
  void testAddEmployee() throws Exception {
    EmployeeDTO newEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    when(employeeService.addEmployee(any(EmployeeDTO.class))).thenReturn(newEmployeeDTO);

    mockMvc.perform(post("/v1/employees")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newEmployeeDTO)))
        .andExpect(status().isCreated())
        .andExpect(content().json(objectMapper.writeValueAsString(newEmployeeDTO)));

    verify(employeeService).addEmployee(newEmployeeDTO);
  }

  @Test
  @WithMockUser(username = "admin")
  void testGetEmployee() throws Exception {
    EmployeeDTO employeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    UUID id = UUID.randomUUID();
    employeeDTO.setEmployeeId(id);
    when(employeeService.getEmployee(id)).thenReturn(employeeDTO);

    mockMvc.perform(get("/v1/employees/{employeeId}", id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(employeeDTO)));

    verify(employeeService).getEmployee(id);
  }

  @Test
  @WithMockUser(username = "admin")
  void testGetAllEmployees() throws Exception {
    List<EmployeeDTO> employeesList = easyRandom.objects(EmployeeDTO.class, 3).toList();
    when(employeeService.getAllEmployees()).thenReturn(employeesList);

    mockMvc.perform(get("/v1/employees")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(employeesList)));

    verify(employeeService).getAllEmployees();
  }

  @Test
  @WithMockUser(username = "admin")
  void testUpdateEmployee() throws Exception {
    EmployeeDTO originalEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    UUID id = UUID.randomUUID();
    originalEmployeeDTO.setEmployeeId(id);
    EmployeeDTO updatedEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    updatedEmployeeDTO.setEmployeeId(id);

    when(employeeService.updateEmployee(any(UUID.class), any(EmployeeDTO.class))).thenReturn(updatedEmployeeDTO);

    mockMvc.perform(put("/v1/employees/{employeeId}", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(originalEmployeeDTO)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(updatedEmployeeDTO)));

    verify(employeeService).updateEmployee(id, originalEmployeeDTO);
  }

  @Test
  @WithMockUser(username = "admin")
  void testDeactivateEmployee() throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc.perform(delete("/v1/employees/{employeeId}", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(employeeService).deactivateEmployee(id);
  }

  // negative case test
  @Test
  @WithMockUser(username = "admin")
  void testGetEmployee_NotFound() throws Exception {
    UUID id = UUID.randomUUID();
    when(employeeService.getEmployee(id)).thenThrow(new NotFoundException("Employee not found"));

    mockMvc.perform(get("/v1/employees/{employeeId}", id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(employeeService).getEmployee(id);
  }
}