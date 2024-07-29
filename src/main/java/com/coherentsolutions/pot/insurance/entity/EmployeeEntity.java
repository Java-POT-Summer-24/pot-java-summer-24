package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class EmployeeEntity extends BaseEntity {

  private String firstName;

  private String lastName;

  private String userName; // unique field

  private String email;

  private LocalDate dateOfBirth;

  private Integer ssn;

  private String phoneNumber;

  private String companyName;

  @Enumerated(EnumType.STRING)
  private EmployeeStatus status;
}
