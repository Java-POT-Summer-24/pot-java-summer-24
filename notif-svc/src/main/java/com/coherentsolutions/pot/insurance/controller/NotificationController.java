package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.entity.NotificationEntity;
import com.coherentsolutions.pot.insurance.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping("/send/{mail}")
  public String sendMail(@PathVariable String mail,
      @RequestBody NotificationEntity notificationEntity) {
    notificationService.sendMail(mail, notificationEntity);
    System.out.println("Received POST request to send notification to " + mail + " with payload: " + notificationEntity);
    notificationService.sendMail(mail, notificationEntity);
    return "Mail sent successfully";
  }
}
