package com.coherentsolutions.pot.insurance.specifications;

import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Sort;

@Data
@ParameterObject
public class SortCriteria {
    private String field;
    private Sort.Direction direction;
}