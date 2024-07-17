package com.coherentsolutions.pot.insurance.dto;

import com.coherentsolutions.pot.insurance.constants.ClaimPlan;
import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDTO implements Serializable {

  private UUID id;

  private String claimNumber;

  private String employee;

  private String company;

  @NotNull(message = "Date of service is required")
  private LocalDate dateOfService;

  @NotNull(message = "Plan type is required")
  private ClaimPlan plan;

  @NotNull(message = "Plan ID is required")
  private UUID planId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
  private double amount;

  @NotNull(message = "Status is required")
  private ClaimStatus status;
}
