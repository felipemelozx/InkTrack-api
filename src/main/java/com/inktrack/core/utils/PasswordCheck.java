package com.inktrack.core.utils;

import java.util.regex.Pattern;

public abstract class PasswordCheck {

  private PasswordCheck() { }

  public static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
  public static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
  public static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
  public static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@#$%^&+=!].*");
  public static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");
}