package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.PackagePayrollFrequency;
import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.constants.PackageType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "package")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

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