package com.coherentsolutions.pot.insurance.specifications;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class SortCriteria {
  private String field;
  private Sort.Direction direction;
}
