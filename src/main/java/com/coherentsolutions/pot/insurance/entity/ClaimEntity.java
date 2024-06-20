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
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEntity {

  @Id
  private UUID id;

  @Column
  private String claimNumber;

  @Column
  private String consumer;

  @Column
  private String employer;

  @Temporal(TemporalType.DATE)
  @Column
  private Date dateOfService;

  @Enumerated(EnumType.STRING)
  @Column
  private Plan plan;

  @Column
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column
  private Status status;

}
