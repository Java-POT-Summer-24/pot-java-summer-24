package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID>,
    JpaSpecificationExecutor<ClaimEntity> {

  //List<ClaimEntity> findByEmployer(String employer);

  //List<ClaimEntity> findByConsumer(String consumer);

  //also commented for now, as i don't have employer or consumer

}
