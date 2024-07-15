package com.coherentsolutions.pot.insurance.microservice.controller;

import com.coherentsolutions.pot.insurance.microservice.dto.NotificationDTO;
import com.coherentsolutions.pot.insurance.microservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  @Autowired
  private final NotificationService notificationService;

  @PostMapping("/send")
  public String sendNotification(@RequestBody NotificationDTO notificationDTO) {
    return notificationService.sendNotification(notificationDTO);
  }
}
