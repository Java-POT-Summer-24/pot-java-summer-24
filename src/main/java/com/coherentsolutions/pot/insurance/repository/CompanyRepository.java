package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID>, JpaSpecificationExecutor<CompanyEntity> {
}
