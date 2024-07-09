package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.CompanyStatus;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import org.springframework.data.jpa.domain.Specification;


public class CompanySpecifications {

    public static Specification<CompanyEntity> byName(String name) {
        return (root, query, cb) -> cb.equal(root.get("name"), name);
    }

    public static Specification<CompanyEntity> byWebsite(String website) {
        return (root, query, cb) -> cb.equal(root.get("website"), website);
    }

    public static Specification<CompanyEntity> byCountryCode(String country_code) {
        return (root, query, cb) -> cb.equal(root.get("country_code"), country_code);
    }

    public static Specification<CompanyEntity> byEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }

    public static Specification<CompanyEntity> byStatus(CompanyStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
