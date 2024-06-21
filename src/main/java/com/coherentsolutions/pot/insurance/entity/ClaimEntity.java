package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.ClaimPlan;
import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
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

  private String claimNumber;

  private String consumer;

  private String employer;

  @Temporal(TemporalType.DATE)
  private Date dateOfService;

  @Enumerated(EnumType.STRING)
  private ClaimPlan plan;

  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  private ClaimStatus status;

}
