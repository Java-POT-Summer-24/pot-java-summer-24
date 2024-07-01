package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

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
    public void deactivateEmployee(@PathVariable UUID employeeId){
        employeeService.deactivateEmployee(employeeId);
    }
}
