package com.coherentsolutions.pot.insurance.specifications;


import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.constants.PlanType;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

@Data
@ParameterObject
public class PlanFilterCriteria {

  private String planName;
  private PlanType planType;
  private PlanStatus status;
}
