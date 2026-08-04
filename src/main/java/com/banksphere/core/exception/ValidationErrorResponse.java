package com.banksphere.core.exception;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
public class ValidationErrorResponse extends ErrorResponse {
    private final Map<String, List<String>> errors;

    public ValidationErrorResponse(LocalDateTime timestamp, int status, String error, String errorCode, String message, String path, String correlationId, Map<String, List<String>> errors) {
        super(timestamp, status, error, errorCode, message, path, correlationId);
        this.errors = errors;
    }
}
