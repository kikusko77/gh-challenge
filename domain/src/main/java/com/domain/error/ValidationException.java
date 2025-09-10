package com.domain.error;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Map<String, String> errors = new LinkedHashMap<>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException addError(String field, String message) {
        errors.put(field, message);
        return this;
    }

    public Map<String, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}