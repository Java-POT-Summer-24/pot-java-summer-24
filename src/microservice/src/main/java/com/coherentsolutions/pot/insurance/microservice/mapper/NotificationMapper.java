package com.coherentsolutions.pot.insurance.microservice.mapper;

import com.coherentsolutions.pot.insurance.microservice.dto.NotificationDTO;
import com.coherentsolutions.pot.insurance.microservice.entity.NotificationEntity;
import org.mapstruct.factory.Mappers;

public interface NotificationMapper {
  NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

  NotificationDTO toDto(NotificationEntity notification);

  NotificationEntity toEntity(NotificationDTO notificationDTO);
}
