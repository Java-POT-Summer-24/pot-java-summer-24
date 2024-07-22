package com.coherentsolutions.pot.insurance.util;

import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class NotificationClient {

  private final WebClient webClient;
  private final String notificationServiceUrl;

  public NotificationClient(WebClient webClient, String notificationServiceUrl) {
    this.webClient = webClient;
    this.notificationServiceUrl = notificationServiceUrl;
  }

  public void sendNotification(String email, Map<String, String> notificationPayload) {
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
