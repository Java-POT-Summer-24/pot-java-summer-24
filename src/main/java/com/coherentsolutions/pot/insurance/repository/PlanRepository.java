package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.coherentsolutions.pot.insurance.constants.PlanStatus;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, UUID>,
    JpaSpecificationExecutor<PlanEntity> {

  boolean existsById(UUID id);

  Optional<PlanEntity> findById(UUID id);

  @Query("SELECT COALESCE(SUM(p.totalLimit), 0) FROM PlanEntity p WHERE p.packageEntity.id = :packageId AND p.status = :status")
  double findSumOfTotalLimitByPackageId(UUID packageId, PlanStatus status);
}
