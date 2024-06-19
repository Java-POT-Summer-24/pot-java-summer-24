package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClaimMapper {

  ClaimMapper INSTANCE = Mappers.getMapper(ClaimMapper.class);

  ClaimDTO claimToClaimDTO(ClaimEntity claim);

  ClaimEntity claimDTOToClaim(ClaimDTO claimDTO);
}
