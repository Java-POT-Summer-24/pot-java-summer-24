package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import lombok.Data;

@Data
public class FilterCriteria {
  private String claimNumber;
  private String consumer;
  private String employer;
  private ClaimStatus status;
}
