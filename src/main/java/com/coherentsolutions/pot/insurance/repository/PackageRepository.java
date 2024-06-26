package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, UUID> {

  Optional<PackageEntity> findByName(String name);

}
