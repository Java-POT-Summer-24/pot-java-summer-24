package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.mappers.EmployeeMapper;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO){
        return EmployeeDTO.builder()
                .employeeId(employeeRepository.save(employeeMapper.employeeDTOToEmployee(employeeDTO)).getEmployeeId())
                .firstName(employeeDTO.getFirstName())
                .lastName(employeeDTO.getLastName())
                .userName(employeeDTO.getUserName())
                .dateOfBirth(employeeDTO.getDateOfBirth())
                .SSN(employeeDTO.getSSN())
                .phoneNumber(employeeDTO.getPhoneNumber())
                .build();
    }
    public EmployeeDTO getEmployee(UUID employeeId){
        return employeeRepository.findById(employeeId)
                .map(employee -> EmployeeDTO.builder()
                        .employeeId(employee.getEmployeeId())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .userName(employee.getUserName())
                        .dateOfBirth(String.valueOf(employee.getDateOfBirth()))
                        .SSN(employee.getSSN())
                        .phoneNumber(employee.getPhoneNumber())
                        .build())
                .orElse(null);
    }

    public List<EmployeeDTO> getAllEmployees(){
        return employeeRepository.findAll().stream()
                .map(employee -> EmployeeDTO.builder()
                        .employeeId(employee.getEmployeeId())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .userName(employee.getUserName())
                        .dateOfBirth(String.valueOf(employee.getDateOfBirth()))
                        .SSN(employee.getSSN())
                        .phoneNumber(employee.getPhoneNumber())
                        .build())
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
                    return EmployeeDTO.builder()
                            .employeeId(employeeRepository.save(employee).getEmployeeId())
                            .firstName(employee.getFirstName())
                            .lastName(employee.getLastName())
                            .userName(employee.getUserName())
                            .dateOfBirth(String.valueOf(employee.getDateOfBirth()))
                            .SSN(employee.getSSN())
                            .phoneNumber(employee.getPhoneNumber())
                            .build();
                })
                .orElse(null);
    }

    public void deleteEmployee(UUID employeeId){
        employeeRepository.deleteById(employeeId);
    }
}
