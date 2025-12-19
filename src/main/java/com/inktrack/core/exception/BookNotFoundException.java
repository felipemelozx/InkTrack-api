package com.inktrack.core.exception;

public class BookNotFoundException extends RuntimeException {
  private final String fieldName;

  public BookNotFoundException(String fieldName, String message) {
    super(message);
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }
}
