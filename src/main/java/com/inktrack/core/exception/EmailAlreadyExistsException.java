package com.inktrack.core.exception;

public class EmailAlreadyExistsException extends RuntimeException {
  public EmailAlreadyExistsException(String email) {
    super("E-mail já está em uso: " + email);
  }
}
