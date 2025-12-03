package com.inktrack.core.exception;

public class FieldDomainValidationException extends RuntimeException {
    private final String fieldName;

    public FieldDomainValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}