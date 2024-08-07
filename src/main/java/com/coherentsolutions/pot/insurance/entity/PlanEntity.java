package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.constants.PlanType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Table(name = "plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PlanEntity extends BaseEntity {

  private String planName;

  @Enumerated(EnumType.STRING)
  private PlanType planType;

  @Temporal(TemporalType.DATE)
  private LocalDate startDate;

  @Temporal(TemporalType.DATE)
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  private PlanStatus status;

  @ManyToOne
  @JoinColumn(name = "package_id", referencedColumnName = "id")
  private PackageEntity packageEntity;

  private double totalLimit;

  private double remainingLimit;
}
