package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClaimMapper {

  ClaimMapper INSTANCE = Mappers.getMapper(ClaimMapper.class);

  ClaimDTO entityToDto(ClaimEntity claim);

  ClaimEntity dtoToEntity(ClaimDTO claimDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "claimNumber", ignore = true)
  void updateClaimFromDTO(ClaimDTO dto, @MappingTarget ClaimEntity entity);
}
