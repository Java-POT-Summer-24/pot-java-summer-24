package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.mappers.EmployeeMapper;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO){
        return employeeMapper
                .employeeToEmployeeDTO(employeeRepository.save(employeeMapper.employeeDTOToEmployee(employeeDTO)));
    }

    public EmployeeDTO getEmployee(UUID employeeId){
        return employeeRepository.findById(employeeId)
                .map(employeeMapper::employeeToEmployeeDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error 404: Employee not found with id: " + employeeId));
    }

    public List<EmployeeDTO> getAllEmployees(){
        return employeeRepository.findAll().stream()
                .map(employeeMapper::employeeToEmployeeDTO)
                .toList();
    }

    public EmployeeDTO updateEmployee(UUID employeeId, EmployeeDTO updatedEmployeeDTO){
        return employeeRepository.findById(employeeId)
                .map(employee -> {
                    employee.setFirstName(updatedEmployeeDTO.getFirstName());
                    employee.setLastName(updatedEmployeeDTO.getLastName());
                    employee.setUserName(updatedEmployeeDTO.getUserName());
                    //employee.setDateOfBirth(updatedEmployeeDTO.getDateOfBirth()); left to fix due to string - date conversion
                    employee.setSSN(updatedEmployeeDTO.getSSN());
                    employee.setPhoneNumber(updatedEmployeeDTO.getPhoneNumber());
                    return employeeMapper.employeeToEmployeeDTO(employeeRepository.save(employee));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error 404: Employee not found with id: " + employeeId));
    }

    public void deleteEmployee(UUID employeeId){
        employeeRepository.deleteById(employeeId);
    }
}