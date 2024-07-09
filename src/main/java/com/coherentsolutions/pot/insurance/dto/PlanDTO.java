package com.coherentsolutions.pot.insurance.dto;

import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.constants.PlanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PlanDTO {

  private UUID id;

  @NotBlank(message = "Plan name is required")
  private String planName;

  @NotNull(message = "Status is required")
  private PlanStatus status;

  @NotNull(message = "Plan type is required")
  private PlanType planType;

  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  private LocalDate endDate;

}
