package com.coherentsolutions.pot.insurance.exception;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

  private int status;
  private String message;
  private Map<String, String> errors;

  public ErrorResponse(int status, String message, String detail) {
    this.status = status;
    this.message = message;
    this.errors = Map.of("detail", detail);
  }
}

