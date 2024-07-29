package com.coherentsolutions.pot.insurance.specifications;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecifications {

  public static Specification<EmployeeEntity> byFirstName(String firstName) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("firstName"),
        firstName);
  }

  public static Specification<EmployeeEntity> byLastName(String lastName) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lastName"), lastName);
  }

  public static Specification<EmployeeEntity> byUserName(String userName) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userName"), userName);
  }

  public static Specification<EmployeeEntity> byDateOfBirth(LocalDate dateOfBirth) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("dateOfBirth"),
        dateOfBirth);
  }

  public static Specification<EmployeeEntity> bySsn(Integer ssn) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ssn"), ssn);
  }

  public static Specification<EmployeeEntity> byStatus(EmployeeStatus status) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
  }

  public static Specification<EmployeeEntity> byCompany(String company) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("company"), company);
  }
}
