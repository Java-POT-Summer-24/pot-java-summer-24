package com.coherentsolutions.pot.insurance.dto;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO implements Serializable {

  private UUID id;

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Username is required")
  private String userName;

  @NotBlank(message = "Email is required")
  private String email;

  @NotNull(message = "Date of birth is required")
  private LocalDate dateOfBirth;

  @NotNull(message = "SSN is required")
  private Integer ssn;

  @NotBlank(message = "Phone number is required")
  private String phoneNumber;

  @NotNull(message = "Status is required")
  private EmployeeStatus status;

  @NotBlank(message = "Company name is required")
  private String companyName;
}
