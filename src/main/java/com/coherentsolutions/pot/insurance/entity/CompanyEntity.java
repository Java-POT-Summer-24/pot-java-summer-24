package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.CompanyStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private String countryCode;

    private String address;

    private String phoneNumber;

    private String email;

    private String website;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;
}
