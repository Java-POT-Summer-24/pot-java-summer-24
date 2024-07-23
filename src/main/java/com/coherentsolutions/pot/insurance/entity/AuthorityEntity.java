package com.coherentsolutions.pot.insurance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "authority")
@Setter
@Getter
public class AuthorityEntity extends BaseEntity {

  private String authority;
  @ManyToOne
  @JoinColumn(name = "userId")
  private UserEntity user;
}
