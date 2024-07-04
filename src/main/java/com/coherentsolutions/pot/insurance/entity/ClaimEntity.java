package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.ClaimPlan;
import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claim")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String claimNumber;

  @ManyToOne
  @JoinColumn(name = "consumer", referencedColumnName = "userName", insertable = false, updatable = false)
  private EmployeeEntity consumer;

  @ManyToOne
  @JoinColumn(name = "employer", referencedColumnName = "name", insertable = false, updatable = false)
  private CompanyEntity employer;

  private LocalDate dateOfService;

  @Enumerated(EnumType.STRING)
  private ClaimPlan plan;

  private double amount;

  @Enumerated(EnumType.STRING)
  private ClaimStatus status;

}
