package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, UUID> {
    boolean existsById(UUID id);
    Optional<PlanEntity> findById(UUID id);
}