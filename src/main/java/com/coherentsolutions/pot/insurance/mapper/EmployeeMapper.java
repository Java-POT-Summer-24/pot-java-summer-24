package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeDTO employeeToEmployeeDTO(EmployeeEntity employee);

    EmployeeEntity employeeDTOToEmployee(EmployeeDTO employeeDTO);

    void updateEmployeeFromDTO(EmployeeDTO dto, @MappingTarget EmployeeEntity entity);
}