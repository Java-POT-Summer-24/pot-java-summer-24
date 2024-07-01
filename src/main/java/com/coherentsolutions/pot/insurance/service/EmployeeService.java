package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.EmployeeMapper;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO){
        return EmployeeMapper.INSTANCE
                .employeeToEmployeeDTO(employeeRepository.save(EmployeeMapper.INSTANCE.employeeDTOToEmployee(employeeDTO)));
    }

    public EmployeeDTO getEmployee(UUID employeeId){
        return employeeRepository.findById(employeeId)
                .map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + employeeId));
    }

    public List<EmployeeDTO> getAllEmployees(){
        return employeeRepository.findAll().stream()
                .map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO)
                .toList();
    }

    public EmployeeDTO updateEmployee(UUID employeeId, EmployeeDTO updatedEmployeeDTO){
        return employeeRepository.findById(employeeId)
                .map(employee -> {
                    if (employee.getStatus() == EmployeeStatus.INACTIVE) {
                        throw new NotFoundException("Cannot update. Employee with id: " + employeeId + " is inactive");
                    }
                    EmployeeMapper.INSTANCE.updateEmployeeFromDTO(updatedEmployeeDTO, employee);
                    return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employeeRepository.save(employee));
                })
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + employeeId));
    }

    public void deleteEmployee(UUID employeeId){
        employeeRepository.findById(employeeId)
                .map(employee -> {
                    if (employee.getStatus() == EmployeeStatus.INACTIVE) {
                        throw new NotFoundException("Cannot delete. Employee with id: " + employeeId + " is already inactive");
                    }
                    employee.setStatus(EmployeeStatus.INACTIVE);
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + employeeId));
    }
}