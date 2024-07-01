package com.coherentsolutions.pot.insurance.specifications;

import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;


@Data
@ParameterObject
public class FilterAndSortCriteria {
  private FilterCriteria filterCriteria;
  private SortCriteria sortCriteria;
}
