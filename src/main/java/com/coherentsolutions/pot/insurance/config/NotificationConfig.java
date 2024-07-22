package com.coherentsolutions.pot.insurance.config;

import com.coherentsolutions.pot.insurance.util.NotificationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NotificationConfig {

  @Value("${notification.service.url:http://localhost:8081/v1/notification/send}")
  private String notificationServiceUrl;

  @Bean
  public WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  public NotificationClient notificationClient(WebClient webClient) {
    return new NotificationClient(webClient, notificationServiceUrl);
  }
}
