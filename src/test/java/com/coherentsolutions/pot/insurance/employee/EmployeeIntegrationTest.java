package com.coherentsolutions.pot.insurance.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import com.coherentsolutions.pot.insurance.controller.EmployeeController;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.mapper.EmployeeMapper;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({EmployeeController.class, EmployeeService.class})
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration"
})
public class EmployeeIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private EmployeeRepository employeeRepository;

  private final EasyRandom easyRandom = new EasyRandom();

  @Test
  @WithMockUser(username = "admin")
  void testGetFilteredSortedEmployees() throws Exception {
    List<EmployeeEntity> employeeEntities = easyRandom.objects(EmployeeEntity.class, 3).toList();
    List<EmployeeDTO> employeeDTOS = employeeEntities.stream()
        .map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO)
        .toList();

    Page<EmployeeDTO> pagedEmployees = new PageImpl<>(employeeDTOS, PageRequest.of(0, 3), employeeDTOS.size());

    Mockito.when(employeeRepository.findAll(any(Specification.class), any(PageRequest.class)))
        .thenReturn(new PageImpl<>(employeeEntities));

    mockMvc.perform(get("/v1/employees/filtered")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("firstName", "Jane")
            .param("lastName", "Doe")
            .param("page", "0")
            .param("size", "3")
            .param("sort", "firstName,asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3))
        .andExpect(jsonPath("$.content[0].firstName").value(employeeDTOS.get(0).getFirstName()))
        .andExpect(jsonPath("$.content[1].firstName").value(employeeDTOS.get(1).getFirstName()))
        .andExpect(jsonPath("$.content[2].firstName").value(employeeDTOS.get(2).getFirstName()));
  }

  @Test
  @WithMockUser(username = "admin")
  void testAddEmployee() throws Exception {
    EmployeeDTO employeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    EmployeeEntity employeeEntity = EmployeeMapper.INSTANCE.employeeDTOToEmployee(employeeDTO);

    Mockito.when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(employeeEntity);

    String employeeJson = objectMapper.writeValueAsString(employeeDTO);
    mockMvc.perform(post("/v1/employees")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(employeeJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value(employeeDTO.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(employeeDTO.getLastName()));
  }

  @Test
  @WithMockUser(username = "admin")
  void testGetAllEmployees() throws Exception {
    List<EmployeeEntity> employeeEntities = easyRandom.objects(EmployeeEntity.class, 3).toList();

    Mockito.when(employeeRepository.findAll()).thenReturn(employeeEntities);

    mockMvc.perform(get("/v1/employees")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].firstName").value(employeeEntities.get(0).getFirstName()));
  }

  @Test
  @WithMockUser(username = "admin")
  void testGetEmployeeById() throws Exception {
    UUID employeeId = UUID.randomUUID();
    EmployeeDTO employeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    employeeDTO.setId(employeeId);
    EmployeeEntity employeeEntity = EmployeeMapper.INSTANCE.employeeDTOToEmployee(employeeDTO);

    Mockito.when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employeeEntity));

    mockMvc.perform(get("/v1/employees/{id}", employeeId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ssn").value(employeeDTO.getSsn()));
  }

  @Test
  @WithMockUser(username = "admin")
  void testUpdateEmployee() throws Exception {
    UUID employeeId = UUID.randomUUID();
    EmployeeDTO employeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    employeeDTO.setId(employeeId);
    EmployeeEntity employeeEntity = EmployeeMapper.INSTANCE.employeeDTOToEmployee(employeeDTO);

    Mockito.when(employeeRepository.findById(eq(employeeId))).thenReturn(Optional.of(employeeEntity));
    Mockito.when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(employeeEntity);

    String employeeJson = objectMapper.writeValueAsString(employeeDTO);
    mockMvc.perform(put("/v1/employees/{id}", employeeId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(employeeJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value(employeeDTO.getFirstName()));
  }

  @Test
  @WithMockUser(username = "admin")
  void testDeactivateEmployee() throws Exception {
    UUID employeeId = UUID.randomUUID();
    EmployeeDTO activeEmployeeDTO = easyRandom.nextObject(EmployeeDTO.class);
    activeEmployeeDTO.setId(employeeId);
    activeEmployeeDTO.setStatus(EmployeeStatus.ACTIVE);
    EmployeeEntity activeEmployeeEntity = EmployeeMapper.INSTANCE.employeeDTOToEmployee(activeEmployeeDTO);

    EmployeeDTO inactiveEmployeeDTO = EmployeeMapper.INSTANCE.employeeToEmployeeDTO(activeEmployeeEntity);
    inactiveEmployeeDTO.setStatus(EmployeeStatus.INACTIVE);
    EmployeeEntity inactiveEmployeeEntity = EmployeeMapper.INSTANCE.employeeDTOToEmployee(inactiveEmployeeDTO);

    // Case 1: Employee is active and gets deactivated
    Mockito.when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(activeEmployeeEntity));
    Mockito.when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(inactiveEmployeeEntity);

    mockMvc.perform(delete("/v1/employees/{id}", employeeId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("INACTIVE"));

    // Case 2: Employee is already inactive
    Mockito.when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(inactiveEmployeeEntity));

    mockMvc.perform(delete("/v1/employees/{id}", employeeId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isNotFound());
  }
}
