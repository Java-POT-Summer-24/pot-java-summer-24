package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClaimMapper {

  ClaimMapper INSTANCE = Mappers.getMapper(ClaimMapper.class);

  @Mapping(source = "consumer.userName", target = "consumer")
  @Mapping(source = "employer.name", target = "employer")
  ClaimDTO entityToDto(ClaimEntity claim);

  @Mapping(source = "consumer", target = "consumer", qualifiedByName = "stringToEmployee")
  @Mapping(source = "employer", target = "employer", qualifiedByName = "stringToCompany")
  ClaimEntity dtoToEntity(ClaimDTO claimDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "claimNumber", ignore = true)
  @Mapping(source = "consumer", target = "consumer", qualifiedByName = "stringToEmployee")
  @Mapping(source = "employer", target = "employer", qualifiedByName = "stringToCompany")
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
}
