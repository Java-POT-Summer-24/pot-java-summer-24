package com.coherentsolutions.pot.insurance.microservice.service;

import com.coherentsolutions.pot.insurance.microservice.dto.NotificationDTO;
import com.coherentsolutions.pot.insurance.microservice.entity.NotificationEntity;
import com.coherentsolutions.pot.insurance.microservice.mapper.NotificationMapper;
import com.coherentsolutions.pot.insurance.microservice.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final JavaMailSender javaMailSender;
  private final NotificationMapper notificationMapper;

  public String sendNotification(NotificationDTO notificationDTO) {
    NotificationEntity notificationEntity = notificationMapper.toEntity(notificationDTO);
    notificationRepository.save(notificationEntity);
    try {
      sendEmail(notificationDTO);
    } catch (MessagingException e) {
      return "Error sending notification: " + e.getMessage();
    }
    return "Notification sent!";
  }

  private void sendEmail(NotificationDTO notificationDTO) throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(notificationDTO.getEmail());
    helper.setSubject("Notification");
    helper.setText(notificationDTO.getMessage(), true);
    javaMailSender.send(message);
  }
}