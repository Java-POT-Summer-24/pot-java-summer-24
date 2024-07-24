package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import com.coherentsolutions.pot.insurance.specifications.EmployeeFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/filtered")
    @ResponseStatus(HttpStatus.OK)
    public Page<EmployeeDTO> getFilteredSortedEmployees(
        @ParameterObject EmployeeFilterCriteria employeeFilterCriteria,
        @ParameterObject Pageable pageable) {
        return employeeService.getFilteredSortedEmployees(employeeFilterCriteria, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO addEmployee(@RequestBody EmployeeDTO employeeDTO){
        return employeeService.addEmployee(employeeDTO);
    }

    @GetMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDTO getEmployee(@PathVariable UUID employeeId){
        return employeeService.getEmployee(employeeId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDTO> getAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @PutMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDTO updateEmployee(@PathVariable UUID employeeId, @RequestBody EmployeeDTO updatedEmployeeDTO){
        return employeeService.updateEmployee(employeeId, updatedEmployeeDTO);
    }

    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDTO deactivateEmployee(@PathVariable UUID employeeId) {
        return employeeService.deactivateEmployee(employeeId);
    }
}
