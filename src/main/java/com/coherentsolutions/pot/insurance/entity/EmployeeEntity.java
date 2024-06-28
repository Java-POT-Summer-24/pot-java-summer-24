package com.coherentsolutions.pot.insurance.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID employeeId;

    private String firstName;

    private String lastName;

    private String userName; // unique field

    private String email;

    private Date dateOfBirth;

    private Integer SSN;

    private Integer phoneNumber;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;
}
