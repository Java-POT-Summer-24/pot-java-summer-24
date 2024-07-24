package com.coherentsolutions.pot.insurance.dto;

import com.coherentsolutions.pot.insurance.entity.AuthorityEntity;
import lombok.Data;

@Data
public class UserDTO {
  private String name;
  private String email;
  private AuthorityEntity authority;
}
