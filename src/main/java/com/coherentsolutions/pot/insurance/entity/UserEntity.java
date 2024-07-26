package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.UserRole;
import com.nimbusds.jose.crypto.opts.UserAuthenticationRequired;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "insuranceUser")
@Setter
@Getter
public class
UserEntity extends BaseEntity {
  private String userName;
  private String email;
  private UserRole role;
  @ManyToOne
  @JoinColumn(name = "companyId")
  private CompanyEntity company;
}
