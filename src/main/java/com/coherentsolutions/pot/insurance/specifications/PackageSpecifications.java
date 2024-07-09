package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.constants.PackagePayrollFrequency;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class PackageSpecifications {

  public static Specification<PackageEntity> byName(String name) {
    return (root, query, cb) -> cb.equal(root.get("name"), name);
  }

  public static Specification<PackageEntity> byStatus(PackageStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<PackageEntity> byStartDate(LocalDate startDate) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
  }

  public static Specification<PackageEntity> byEndDate(LocalDate endDate) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), endDate);
  }

  public static Specification<PackageEntity> byPayrollFrequency(PackagePayrollFrequency payrollFrequency) {
    return (root, query, cb) -> cb.equal(root.get("payrollFrequency"), payrollFrequency);
  }
}