package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.PackagePayrollFrequency;
import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.constants.PackageType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "package")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PackageEntity extends BaseEntity {

  private String name;

  @Enumerated(EnumType.STRING)
  private PackageStatus status;

  @Enumerated(EnumType.STRING)
  private PackagePayrollFrequency payrollFrequency;

  private LocalDate startDate;

  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  private PackageType type;

  private double contributions;

}