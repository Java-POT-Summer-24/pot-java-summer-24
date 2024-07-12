package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

@Data
@ParameterObject
public class ClaimFilterCriteria {
  private String claimNumber;
  private String employee;
  private String company;
  private ClaimStatus status;
}
