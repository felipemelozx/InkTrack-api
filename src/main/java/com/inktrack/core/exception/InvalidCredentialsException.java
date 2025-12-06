package com.inktrack.core.exception;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException() {
    super("Email or password is invalid.");
  }
}
