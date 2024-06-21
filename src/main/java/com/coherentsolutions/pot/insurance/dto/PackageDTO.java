package com.coherentsolutions.pot.insurance.dto;

import com.coherentsolutions.pot.insurance.entity.PayrollFrequency;
import com.coherentsolutions.pot.insurance.entity.Status;
import com.coherentsolutions.pot.insurance.entity.Type;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO {

  private UUID id;

  @NotNull(message = "Name cannot be null")
  private String name;

  @NotNull(message = "Status cannot be null")
  private Status status;

  @NotNull(message = "Payroll frequency cannot be null")
  private PayrollFrequency payrollFrequency;

  @NotNull(message = "Start date cannot be null")
  private Date startDate;

  @NotNull(message = "End date cannot be null")
  private Date endDate;

  @NotNull(message = "Type cannot be null")
  private Type type;

  @NotNull(message = "Contributions cannot be null")
  @DecimalMin(value = "0.01", message = "Contributions must be greater than zero")
  private BigDecimal contributions;
}
