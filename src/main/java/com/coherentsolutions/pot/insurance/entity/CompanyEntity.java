package com.coherentsolutions.pot.insurance.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@Table(name = "COMPANY")
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column()
    private String name;

    @Column()
    private String countryCode;

    @Column()
    private String address;

    @Column()
    private String phoneNumber;

    @Column()
    private String email;

    @Column()
    private String website;

    @Column()
    private String status;

    public CompanyEntity(String name, String countryCode, String address, String phoneNumber, String email, String website, String status) {
        this.name = name;
        this.countryCode = countryCode;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.status = status;
    }
}
