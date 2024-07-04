package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.PlanStatus;
import com.coherentsolutions.pot.insurance.constants.PlanType;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import org.springframework.data.jpa.domain.Specification;

public class PlanSpecifications {

  public static Specification<PlanEntity> byName(String name) {
    return (root, query, cb) -> cb.equal(root.get("planName"), name);
  }

  public static Specification<PlanEntity> byStatus(PlanStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<PlanEntity> byType(PlanType type) {
    return (root, query, cb) -> cb.equal(root.get("planType"), type);
  }
}
