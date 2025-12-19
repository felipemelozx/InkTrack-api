package com.inktrack.core.utils;

import com.inktrack.core.exception.FieldDomainValidationException;
import com.inktrack.core.usecases.user.CreateUserRequestModel;

public final class Validation {

  private Validation() {}

  public static void validate(CreateUserRequestModel input) {
    if (input.name() == null || input.name().isBlank()) {
      throw new FieldDomainValidationException("name", "Name is required");
    }

    if(!isStrongPassword(input.passwordRaw())) {
      throw new FieldDomainValidationException("password", "Password must be stronger");
    }

    if (!isValidEmail(input.email())) {
      throw new FieldDomainValidationException("email", "Invalid email format");
    }
  }

  public static boolean isValidEmail(String email) {
    if (email == null) {
      return false;
    }

    return email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$");
  }

  public static boolean isStrongPassword(String password) {
    if (password == null) {
      return false;
    }

    return PasswordCheck.LENGTH_PATTERN.matcher(password).find()
        && PasswordCheck.UPPERCASE_PATTERN.matcher(password).find()
        && PasswordCheck.DIGIT_PATTERN.matcher(password).find()
        && PasswordCheck.LOWERCASE_PATTERN.matcher(password).find()
        && PasswordCheck.SPECIAL_CHAR_PATTERN.matcher(password).find();
  }
}
