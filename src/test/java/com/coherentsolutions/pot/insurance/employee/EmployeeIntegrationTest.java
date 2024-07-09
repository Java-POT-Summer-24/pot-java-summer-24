package com.coherentsolutions.pot.insurance.employee;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.controller.EmployeeController;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
