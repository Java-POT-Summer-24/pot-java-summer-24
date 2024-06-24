package com.coherentsolutions.pot.insurance.dto;

import com.coherentsolutions.pot.insurance.dto.enums.*;
import jakarta.validation.constraints.NotBlank;
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
    private UUID planId;

    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotBlank(message = "Status is required")
    private PlanStatus status;

    @NotBlank(message = "Plan type is required")
    private PlanType planType;

    @NotBlank(message = "Payroll frequency is required")
    private PayrollFrequency payrollFrequency;

    @NotBlank(message = "Start date is required")
    private LocalDate startDate;

    @NotBlank(message = "End date is required")
    private LocalDate endDate;

}
