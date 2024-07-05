package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.ClaimPlan;
import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claim")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ClaimEntity extends BaseEntity {

  private String claimNumber;

  @ManyToOne
  @JoinColumn(name = "employee", referencedColumnName = "userName")
  private EmployeeEntity employee;

  @ManyToOne
  @JoinColumn(name = "company", referencedColumnName = "name")
  private CompanyEntity company;

  private LocalDate dateOfService;

  @Enumerated(EnumType.STRING)
  private ClaimPlan plan;

  private double amount;

  @Enumerated(EnumType.STRING)
  private ClaimStatus status;

}
