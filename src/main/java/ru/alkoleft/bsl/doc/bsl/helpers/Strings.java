package ru.alkoleft.bsl.doc.bsl.helpers;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Strings {
  public boolean isNullOrEmpty(String value) {
    return value == null || value.isEmpty();
  }
}
