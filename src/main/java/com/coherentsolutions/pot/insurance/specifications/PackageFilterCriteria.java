package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.PackagePayrollFrequency;
import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import java.time.LocalDate;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

@Data
@ParameterObject
public class PackageFilterCriteria {
  private String name;
  private PackageStatus status;
  private LocalDate startDate;
  private LocalDate endDate;
  private PackagePayrollFrequency payrollFrequency;
}
