package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import org.springframework.data.jpa.domain.Specification;


public class ClaimSpecifications {

  public static Specification<ClaimEntity> byClaimNumber(String claimNumber) {
    return (root, query, cb) -> cb.equal(root.get("claimNumber"), claimNumber);
  }

  public static Specification<ClaimEntity> byEmployeeUserName(String employeeUserName) {
    return (root, query, cb) -> cb.equal(root.get("employee").get("userName"), employeeUserName);
  }

  public static Specification<ClaimEntity> byCompanyName(String companyName) {
    return (root, query, cb) -> cb.equal(root.get("company").get("name"), companyName);
  }

  public static Specification<ClaimEntity> byStatus(ClaimStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

}
