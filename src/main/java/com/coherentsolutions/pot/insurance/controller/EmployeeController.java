package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import com.coherentsolutions.pot.insurance.specifications.EmployeeFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("@securityService.canGetFilteredSortedEmployees(authentication)")
    public Page<EmployeeDTO> getFilteredSortedEmployees(
        @ParameterObject EmployeeFilterCriteria employeeFilterCriteria,
        @ParameterObject Pageable pageable) {
        return employeeService.getFilteredSortedEmployees(employeeFilterCriteria, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@securityService.canAddEmployee(authentication)")
    @CacheEvict(value = "employeesList", allEntries = true)
    public EmployeeDTO addEmployee(@RequestBody EmployeeDTO employeeDTO){
        return employeeService.addEmployee(employeeDTO);
    }

    @GetMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.canAccessEmployee(authentication, #employeeId)")
    @Cacheable(value = "employee", key = "#employeeId")
    public EmployeeDTO getEmployee(@PathVariable UUID employeeId){
        return employeeService.getEmployee(employeeId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.canGetAllEmployees(authentication)")
    @Cacheable(value = "employeesList")
    public List<EmployeeDTO> getAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @PutMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.canUpdateEmployee(authentication, #employeeId)")
    @Caching(
        evict = {@CacheEvict(value = "employeesList", allEntries = true)},
        put = {@CachePut(value = "employee", key = "#employeeId")}
    )
    public EmployeeDTO updateEmployee(@PathVariable UUID employeeId, @RequestBody EmployeeDTO updatedEmployeeDTO){
        return employeeService.updateEmployee(employeeId, updatedEmployeeDTO);
    }

    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.canDeactivateEmployee(authentication, #employeeId)")
    @Caching(
        evict = {@CacheEvict(value = "employeesList", allEntries = true)},
        put = {@CachePut(value = "employee", key = "#employeeId")}
    )
    public EmployeeDTO deactivateEmployee(@PathVariable UUID employeeId) {
        return employeeService.deactivateEmployee(employeeId);
    }
}
