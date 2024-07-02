package com.coherentsolutions.pot.insurance.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

  private final Map<String, String> validationErrors;

  public ValidationException(String message, Map<String, String> validationErrors) {
    super(message);
    this.validationErrors = validationErrors;
  }

}