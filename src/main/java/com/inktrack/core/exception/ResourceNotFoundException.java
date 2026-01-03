package com.inktrack.core.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final String field;

    public ResourceNotFoundException(String resource, String field, String message) {
        super(message);
        this.resource = resource;
        this.field = field;
    }

    public String getResource() {
        return resource;
    }

    public String getField() {
        return field;
    }
}
