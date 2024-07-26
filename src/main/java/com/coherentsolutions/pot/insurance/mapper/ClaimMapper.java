package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClaimMapper {

  ClaimMapper INSTANCE = Mappers.getMapper(ClaimMapper.class);

  @Mapping(source = "employee.userName", target = "employeeUserName")
  @Mapping(source = "company.name", target = "company")
  @Mapping(source = "planEntity.id", target = "planId")
  ClaimDTO entityToDto(ClaimEntity claim);

  @Mapping(source = "employeeUserName", target = "employee", qualifiedByName = "stringToEmployee")
  @Mapping(source = "company", target = "company", qualifiedByName = "stringToCompany")
  @Mapping(source = "planId", target = "planEntity", qualifiedByName = "uuidToPlan")
  ClaimEntity dtoToEntity(ClaimDTO claimDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "claimNumber", ignore = true)
  @Mapping(target = "employee", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "planEntity", ignore = true)
  void updateClaimFromDTO(ClaimDTO dto, @MappingTarget ClaimEntity entity);

  @Named("stringToEmployee")
  default EmployeeEntity stringToEmployee(String userName) {
    if (userName == null) {
      return null;
    }
    EmployeeEntity employee = new EmployeeEntity();
    employee.setUserName(userName);
    return employee;
  }

  @Named("stringToCompany")
  default CompanyEntity stringToCompany(String name) {
    if (name == null) {
      return null;
    }
    CompanyEntity company = new CompanyEntity();
    company.setName(name);
    return company;
  }

  @Named("uuidToPlan")
  default PlanEntity uuidToPlan(UUID planId) {
    if (planId == null) {
      return null;
    }
    PlanEntity plan = new PlanEntity();
    plan.setId(planId);
    return plan;
  }
}
