package com.coherentsolutions.pot.insurance.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private UUID employeeId;
    private String firstName;
    private String lastName;
    private String userName;
    private String dateOfBirth;
    private Integer SSN;
    private Integer phoneNumber;
}
