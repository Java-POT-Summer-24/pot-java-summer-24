package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID>,
    JpaSpecificationExecutor<ClaimEntity> {

  List<ClaimEntity> findAllByEmployeeUserName(String employeeUserName);

  List<ClaimEntity> findAllByCompanyName(String companyName);

}
