package com.coherentsolutions.pot.insurance.dto;

import com.coherentsolutions.pot.insurance.constants.PackagePayrollFrequency;
import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.constants.PackageType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO implements Serializable {

  private UUID id;

  @NotNull(message = "Name cannot be null")
  private String name;

  @NotNull(message = "Status cannot be null")
  private PackageStatus status;

  @NotNull(message = "Payroll frequency cannot be null")
  private PackagePayrollFrequency payrollFrequency;

  @NotNull(message = "Start date cannot be null")
  private LocalDate startDate;

  @NotNull(message = "End date cannot be null")
  private LocalDate endDate;

  @NotNull(message = "Type cannot be null")
  private PackageType type;

  @NotNull(message = "Contributions cannot be null")
  @DecimalMin(value = "0.01", message = "Contributions must be greater than zero")
  private double contributions;
}
