package com.coherentsolutions.pot.insurance.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtil {
  public static <T> boolean isNotEmpty(T value) {
    return value != null;
  }

  public static boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }
}
