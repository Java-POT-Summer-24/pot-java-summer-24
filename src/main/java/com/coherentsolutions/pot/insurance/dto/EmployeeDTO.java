package com.coherentsolutions.pot.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
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
    private String email;
    private LocalDate dateOfBirth;
    private Integer SSN;
    private Integer phoneNumber;
}
