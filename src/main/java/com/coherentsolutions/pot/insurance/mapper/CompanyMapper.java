package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    CompanyDTO toDTO(CompanyEntity companyEntity);
    CompanyEntity toEntity(CompanyDTO companyDTO);

    @Mapping(target = "id", ignore = true)
    void updateCompanyFromDTO(CompanyDTO dto, @MappingTarget CompanyEntity entity);
}
