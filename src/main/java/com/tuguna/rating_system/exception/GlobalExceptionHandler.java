package com.tuguna.rating_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> build(HttpStatus status, Object message, ErrorCode code) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("errorCode", code);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, status);
    }
    // Handle custom ApiException
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException ex) {
        HttpStatus status = mapErrorCodeToStatus(ex.getCode());
        return build(status, ex.getMessage(), ex.getCode());
    }
    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return build(HttpStatus.BAD_REQUEST, errors, ErrorCode.VALIDATION_ERROR);
    }
    private HttpStatus mapErrorCodeToStatus(ErrorCode code) {
        return switch (code) {
            case EMAIL_ALREADY_EXISTS -> HttpStatus.CONFLICT;         // 409
            case INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED;       // 401
            case EMAIL_NOT_VERIFIED -> HttpStatus.FORBIDDEN;           // 403
            case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;           // 404
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;           // 400
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;                // 403
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}