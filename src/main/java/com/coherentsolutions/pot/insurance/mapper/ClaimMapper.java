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
import java.util.List;

@Mapper
public interface ClaimMapper {

  ClaimMapper INSTANCE = Mappers.getMapper(ClaimMapper.class);

  @Mapping(source = "employee.userName", target = "employee")
  @Mapping(source = "company.name", target = "company")
  ClaimDTO entityToDto(ClaimEntity claim);

  @Mapping(source = "employee", target = "employee", qualifiedByName = "stringToEmployee")
  @Mapping(source = "company", target = "company", qualifiedByName = "stringToCompany")
  ClaimEntity dtoToEntity(ClaimDTO claimDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "claimNumber", ignore = true)
  @Mapping(target = "employee", ignore = true)
  @Mapping(target = "company", ignore = true)
  void updateClaimFromDTO(ClaimDTO dto, @MappingTarget ClaimEntity entity);

  List<ClaimDTO> entitiesToDtos(List<ClaimEntity> claims);
  List<ClaimEntity> dtosToEntities(List<ClaimDTO> claimDTOs);

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
