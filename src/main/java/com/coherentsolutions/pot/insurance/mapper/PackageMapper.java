package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageMapper {

  PackageMapper INSTANCE = Mappers.getMapper(PackageMapper.class);

  PackageDTO entityToDto(PackageEntity entity);
  PackageEntity dtoToEntity(PackageDTO dto);

  @Mapping(target = "id", ignore = true)
  void updatePackageFromDTO(PackageDTO dto, @MappingTarget PackageEntity entity);
}
