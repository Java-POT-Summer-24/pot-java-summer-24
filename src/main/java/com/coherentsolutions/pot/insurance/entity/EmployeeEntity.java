package com.coherentsolutions.pot.insurance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEntity {
    @Id
    private UUID employeeId;

    private String firstName;

    private String lastName;

    private String userName; // unique field

    private Date dateOfBirth;

    private Integer SSN;

    private Integer phoneNumber;
}
