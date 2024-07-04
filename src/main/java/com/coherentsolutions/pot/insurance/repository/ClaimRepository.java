package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID> {

  List<ClaimEntity> findByConsumer_UserName(String consumer);

  List<ClaimEntity> findByEmployer_Name(String employer);

}
