package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlanMapper {
    PlanMapper INSTANCE = Mappers.getMapper(PlanMapper.class);
    PlanDTO toPlanDto(PlanEntity plan);
    PlanEntity toPlanEntity(PlanDTO planDTO);

    @Mapping(target = "planId", ignore = true)
    void updatePlanFromDTO(PlanDTO dto, @MappingTarget PlanEntity entity);
}
