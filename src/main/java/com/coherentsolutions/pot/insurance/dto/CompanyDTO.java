package com.coherentsolutions.pot.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CompanyDTO {
    private Integer id;
    private String name;
    private String countryCode;
    private String address;
    private String phoneNumber;
    private String email;
    private String website;
    private String status;
}