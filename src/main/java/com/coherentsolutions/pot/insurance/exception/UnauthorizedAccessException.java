package com.coherentsolutions.pot.insurance.exception;

public class UnauthorizedAccessException extends RuntimeException {

  public UnauthorizedAccessException(String message) {
    super(message);
  }
}