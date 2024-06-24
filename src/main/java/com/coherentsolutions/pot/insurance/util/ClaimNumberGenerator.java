package com.coherentsolutions.pot.insurance.util;
import java.util.UUID;


public class ClaimNumberGenerator {

  public static String generate() {
    return "BY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}