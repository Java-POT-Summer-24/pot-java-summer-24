package com.coherentsolutions.pot.insurance.entity;

import com.coherentsolutions.pot.insurance.constants.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {

    @Id
    private UUID id;

    private String name;

    private String countryCode;

    private String address;

    private String phoneNumber;

    private String email;

    private String website;

    @Enumerated(EnumType.STRING)
    private Status status;
}
