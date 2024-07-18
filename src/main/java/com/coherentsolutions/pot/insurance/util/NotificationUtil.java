package com.coherentsolutions.pot.insurance.util;

import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class NotificationUtil {

  private static final WebClient webClient = WebClient.create();
  private static final String NOTIFICATION_SERVICE_URL = "http://localhost:8081/v1/notification/send";

  public static void sendNotification(String email, Map<String, String> notificationPayload) {
    webClient.post().uri(NOTIFICATION_SERVICE_URL + "/" + email)
        .body(Mono.just(notificationPayload), Map.class).retrieve().bodyToMono(String.class)
        .block();
  }

  public static void sendDeactivationNotification(String email, String subject, String message) {
    Map<String, String> notificationPayload = Map.of("subject", subject, "message", message);
    sendNotification(email, notificationPayload);
  }
}