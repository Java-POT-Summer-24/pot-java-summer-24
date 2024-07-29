package com.coherentsolutions.pot.insurance.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class NotificationClient {

  private final WebClient webClient;
  private final String notificationServiceUrl;

  public NotificationClient(WebClient.Builder webClientBuilder,
      @Value("${notification.service.url:http://localhost:8082/v1/notification/send}") String notificationServiceUrl) {
    this.webClient = webClientBuilder.build();
    this.notificationServiceUrl = notificationServiceUrl;
  }

  public void sendNotification(String email, Map<String, String> notificationPayload) {
    System.out.println("Sending POST request to: " + notificationServiceUrl + "/" + email);
    System.out.println("Payload: " + notificationPayload);

    webClient.post()
        .uri(notificationServiceUrl + "/" + email)
        .body(Mono.just(notificationPayload), Map.class)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  public void sendDeactivationNotification(String email, String subject, String message) {
    Map<String, String> notificationPayload = Map.of("subject", subject, "message", message);
    sendNotification(email, notificationPayload);
  }
}