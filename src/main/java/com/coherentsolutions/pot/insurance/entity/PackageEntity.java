package com.coherentsolutions.pot.insurance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageEntity {

  @Id
  private UUID id;

  private String name;

  @Enumerated(EnumType.STRING)
  private Status status; //can be enum

  @Enumerated(EnumType.STRING)
  private PayrollFrequency payrollFrequency; // can be enum

  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Temporal(TemporalType.DATE)
  private Date endDate;

  @Enumerated(EnumType.STRING)
  private Type type;//?????????

  private BigDecimal contributions;


}