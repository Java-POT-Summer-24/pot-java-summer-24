package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.ClaimStatus;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import org.springframework.data.jpa.domain.Specification;


public class ClaimSpecifications {

  public static Specification<ClaimEntity> byClaimNumber(String claimNumber) {
    return (root, query, cb) -> cb.equal(root.get("claimNumber"), claimNumber);
  }

  public static Specification<ClaimEntity> byConsumer(String consumer) {
    return (root, query, cb) -> cb.equal(root.get("consumer"), consumer);
  }

  public static Specification<ClaimEntity> byEmployer(String employer) {
    return (root, query, cb) -> cb.equal(root.get("employer"), employer);
  }

  public static Specification<ClaimEntity> byStatus(ClaimStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

}
