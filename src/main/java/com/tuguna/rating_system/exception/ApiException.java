package com.tuguna.rating_system.exception;

public class ApiException extends RuntimeException {
    private final ErrorCode code;
    public ApiException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
    public ErrorCode getCode() {
        return code;
    }
}