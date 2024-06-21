package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageMapper {

  PackageMapper INSTANCE = Mappers.getMapper(PackageMapper.class);

  PackageDTO packageToPackageDTO(PackageEntity entity);
  PackageEntity packageDTOToPackage(PackageDTO dto);
}
