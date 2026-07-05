package com.casualapp.backend.controller;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    private int status;
    private String error;
    private String code;
    private String message;
    private LocalDateTime timestamp;

    public ApiErrorResponse(int status, String error, String code, String message) {
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}