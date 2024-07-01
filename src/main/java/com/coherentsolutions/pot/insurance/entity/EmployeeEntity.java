package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
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

    private LocalDate dateOfBirth;

    private Integer ssn;

    private Integer phoneNumber;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
//    @Column(name = "is_deleted")
//    private boolean isDeleted = false;
}
