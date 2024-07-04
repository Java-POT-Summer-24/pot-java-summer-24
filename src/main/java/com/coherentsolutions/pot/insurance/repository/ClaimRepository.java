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

  @Query("SELECT c FROM ClaimEntity c WHERE c.consumer.userName = :consumer")
  List<ClaimEntity> findByConsumer(@Param("consumer") String consumer);

  @Query("SELECT c FROM ClaimEntity c WHERE c.employer.name = :employer")
  List<ClaimEntity> findByEmployer(@Param("employer") String employer);

  //prob optional
}
