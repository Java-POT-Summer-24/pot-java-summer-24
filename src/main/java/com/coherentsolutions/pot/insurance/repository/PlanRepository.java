package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, UUID>,
    JpaSpecificationExecutor<PlanEntity> {

  boolean existsById(UUID id);

  Optional<PlanEntity> findById(UUID id);

  @Query("SELECT COALESCE(SUM(p.totalLimit), 0) FROM PlanEntity p WHERE p.packageId.id = :packageId")
  double findSumOfTotalLimitByPackageId(@Param("packageId") UUID packageId);
}
