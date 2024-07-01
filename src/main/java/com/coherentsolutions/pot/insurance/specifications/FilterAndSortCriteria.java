package com.coherentsolutions.pot.insurance.specifications;

import lombok.Data;

@Data
public class FilterAndSortCriteria {
  private FilterCriteria filterCriteria;
  private SortCriteria sortCriteria;
}
