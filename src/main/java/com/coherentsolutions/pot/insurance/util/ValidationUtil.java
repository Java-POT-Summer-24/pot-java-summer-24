package com.coherentsolutions.pot.insurance.util;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import java.time.LocalDate;

public class ValidationUtil {
  public static <T> boolean isNotEmpty(T value) {
    return value != null;
  }

  public static boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }
}
