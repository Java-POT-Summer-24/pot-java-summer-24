package com.coherentsolutions.pot.insurance.microservice.repository;

import com.coherentsolutions.pot.insurance.microservice.entity.NotificationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

}
