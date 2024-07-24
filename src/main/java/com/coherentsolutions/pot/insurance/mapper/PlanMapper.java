package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface PlanMapper {
    PlanMapper INSTANCE = Mappers.getMapper(PlanMapper.class);
    @Mapping(source = "packageEntity.id", target="packageId")
    PlanDTO toPlanDto(PlanEntity plan);
    @Mapping(source = "packageId", target="packageEntity", qualifiedByName = "uuidToPackage")
    PlanEntity toPlanEntity(PlanDTO planDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target="packageEntity", ignore = true)
    void updatePlanFromDTO(PlanDTO dto, @MappingTarget PlanEntity entity);

    @Named("uuidToPackage")
    default PackageEntity uuidToPackage(UUID packageId) {
        if (packageId == null) {
            return null;
        }
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setId(packageId);
        return packageEntity;
    }
}
