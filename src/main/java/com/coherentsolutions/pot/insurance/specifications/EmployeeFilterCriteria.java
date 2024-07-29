package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import java.time.LocalDate;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

@Data
@ParameterObject
public class EmployeeFilterCriteria {

  private String firstName;
  private String lastName;
  private String userName;
  private LocalDate dateOfBirth;
  private Integer ssn;
  private EmployeeStatus status;
  private String companyName;
}
