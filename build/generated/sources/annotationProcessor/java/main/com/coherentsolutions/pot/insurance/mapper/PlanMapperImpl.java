package com.coherentsolutions.pot.insurance.mapper;

import com.coherentsolutions.pot.insurance.dto.PlanDTO;
import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-26T08:59:54+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 21.0.2 (BellSoft)"
)
public class PlanMapperImpl implements PlanMapper {

    @Override
    public PlanDTO toPlanDto(PlanEntity plan) {
        if ( plan == null ) {
            return null;
        }

        PlanDTO.PlanDTOBuilder planDTO = PlanDTO.builder();

        planDTO.planId( plan.getPlanId() );
        planDTO.planName( plan.getPlanName() );
        planDTO.status( plan.getStatus() );
        planDTO.planType( plan.getPlanType() );
        planDTO.payrollFrequency( plan.getPayrollFrequency() );
        planDTO.startDate( plan.getStartDate() );
        planDTO.endDate( plan.getEndDate() );

        return planDTO.build();
    }

    @Override
    public PlanEntity toPlanEntity(PlanDTO planDTO) {
        if ( planDTO == null ) {
            return null;
        }

        PlanEntity.PlanEntityBuilder planEntity = PlanEntity.builder();

        planEntity.planId( planDTO.getPlanId() );
        planEntity.planName( planDTO.getPlanName() );
        planEntity.payrollFrequency( planDTO.getPayrollFrequency() );
        planEntity.planType( planDTO.getPlanType() );
        planEntity.startDate( planDTO.getStartDate() );
        planEntity.endDate( planDTO.getEndDate() );
        planEntity.status( planDTO.getStatus() );

        return planEntity.build();
    }

    @Override
    public void updatePlanFromDTO(PlanDTO dto, PlanEntity entity) {
        if ( dto == null ) {
            return;
        }

        entity.setPlanName( dto.getPlanName() );
        entity.setPayrollFrequency( dto.getPayrollFrequency() );
        entity.setPlanType( dto.getPlanType() );
        entity.setStartDate( dto.getStartDate() );
        entity.setEndDate( dto.getEndDate() );
        entity.setStatus( dto.getStatus() );
    }
}
