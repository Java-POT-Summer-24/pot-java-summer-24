package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.CompanyStatus;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

@Data
@ParameterObject
public class CompanyFilterCriteria {
    private String name;
    private String website;
    private String countryCode;
    private String email;
    private CompanyStatus status;
}