package com.coherentsolutions.pot.insurance.microservice.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
  private UUID id;

  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Message is required")
  private String message;
}
