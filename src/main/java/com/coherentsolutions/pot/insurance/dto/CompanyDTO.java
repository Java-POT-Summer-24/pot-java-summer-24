package com.coherentsolutions.pot.insurance.dto;

import com.coherentsolutions.pot.insurance.constants.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO implements Serializable {
    private UUID id;
    private String name;
    private String countryCode;
    private String address;
    private String phoneNumber;
    private String email;
    private String website;
    private CompanyStatus status;
}