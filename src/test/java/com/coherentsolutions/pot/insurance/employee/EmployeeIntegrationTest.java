package com.coherentsolutions.pot.insurance.employee;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import com.coherentsolutions.pot.insurance.controller.EmployeeController;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmployeeController.class)
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class EmployeeIntegrationTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private EmployeeService employeeService;
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetFilteredSortedEmployees() throws Exception {
    List<EmployeeDTO> packages = List.of(
        createBasicEmployeeDTO().firstName("Jane").build(),
        createBasicEmployeeDTO().firstName("John").build()
    );
    Page<EmployeeDTO> pagedEmployees = new PageImpl<>(packages, PageRequest.of(0, 3), packages.size());

    Mockito.when(employeeService.getFilteredSortedEmployees(Mockito.any(), Mockito.any()))
        .thenReturn(pagedEmployees);

    mockMvc.perform(get("/v1/employees/filtered")
            .param("firstName", "Jane")
            .param("lastName", "Doe")
            .param("page", "0")
            .param("size", "3")
            .param("sort", "firstName,asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].firstName").value("Jane"))
        .andExpect(jsonPath("$.content[1].firstName").value("John"));
  }

  @Test
  void testAddEmployee() throws Exception {
    EmployeeDTO employeeDTO = createBasicEmployeeDTO().build();
    String employeeJson = objectMapper.writeValueAsString(employeeDTO);
    Mockito.when(employeeService.addEmployee(Mockito.any())).thenReturn(employeeDTO);
    mockMvc.perform(post("/v1/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(employeeJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Lukas"))
        .andExpect(jsonPath("$.lastName").value("Karlsson"));
  }

  @Test
  void testGetAllEmployees() throws Exception {
    mockMvc.perform(get("/v1/employees")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andReturn();
  }

  @Test
  void testGetEmployeeById() throws Exception {
    UUID employeeId = UUID.fromString("d5e38eb0-58a3-4a0f-878c-72eac1877d5e");
    EmployeeDTO employeeDTO = createBasicEmployeeDTO()
        .id(employeeId)
        .build();
    Mockito.when(employeeService.getEmployee(employeeId)).thenReturn(employeeDTO);
    mockMvc.perform(get("/v1/employees/{id}", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(employeeId.toString()));
  }

  @Test
  void testUpdateEmployee() throws Exception {
    UUID employeeId = UUID.fromString("d5e38eb0-58a3-4a0f-878c-72eac1877d5e");
    EmployeeDTO employeeDTO = createBasicEmployeeDTO()
        .id(employeeId)
        .firstName("Jim")
        .build();
    String packageJson = objectMapper.writeValueAsString(employeeDTO);

    Mockito.when(employeeService.updateEmployee(Mockito.eq(employeeId), Mockito.any(EmployeeDTO.class)))
        .thenReturn(employeeDTO);

    mockMvc.perform(put("/v1/employees/{id}", employeeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(packageJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Jim"))
        .andReturn();
  }

  @Test
  void testDeactivateEmployee() throws Exception {
    UUID employeeId = UUID.fromString("d5e38eb0-58a3-4a0f-878c-72eac1877d5e");
    EmployeeDTO employeeDTO = createBasicEmployeeDTO()
        .id(employeeId)
        .status(EmployeeStatus.INACTIVE)
        .build();

    Mockito.when(employeeService.deactivateEmployee(employeeId)).thenReturn(employeeDTO);

    mockMvc.perform(delete("/v1/employees/{id}", employeeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("INACTIVE"));
  }

  private EmployeeDTO.EmployeeDTOBuilder createBasicEmployeeDTO() {
    return EmployeeDTO.builder()
        .id(UUID.randomUUID())
        .firstName("Lukas")
        .lastName("Karlsson")
        .userName("lukas.karlsson")
        .email("lukas.karlsson@gmail.com")
        .dateOfBirth(LocalDate.of(1990, 1, 1))
        .ssn(123456789)
        .phoneNumber("1234567890");
  }
}
