package com.coherentsolutions.pot.insurance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "user")
@Setter
@Getter
public class UserEntity extends BaseEntity {
  private String name;
  private String email;

  @OneToMany(mappedBy = "user")
  private Set<AuthorityEntity> authorities;

  @ManyToOne
  @JoinColumn(name = "companyId")
  private CompanyEntity company;
}
