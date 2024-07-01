package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.dto.enums.PayrollFrequency;
import com.coherentsolutions.pot.insurance.dto.enums.PlanStatus;
import com.coherentsolutions.pot.insurance.dto.enums.PlanType;
import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PlanEntity extends BaseEntity {

    private String planName;

    @Enumerated(EnumType.STRING)
    private PayrollFrequency payrollFrequency;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PlanStatus status;
}
