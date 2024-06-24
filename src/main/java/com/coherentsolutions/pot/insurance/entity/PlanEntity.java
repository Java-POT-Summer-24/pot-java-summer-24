package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.dto.enums.PayrollFrequency;
import com.coherentsolutions.pot.insurance.dto.enums.PlanStatus;
import com.coherentsolutions.pot.insurance.dto.enums.PlanType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID planId;

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
