package com.coherentsolutions.pot.insurance.specifications;

import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

@Data
@ParameterObject
public class PackageFilterAndSortCriteria {
  private PackageFilterCriteria packageFilterCriteria;
  private SortCriteria sortCriteria;
}