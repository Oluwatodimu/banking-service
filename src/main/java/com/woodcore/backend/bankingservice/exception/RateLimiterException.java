package com.woodcore.backend.bankingservice.exception;

public class RateLimiterException extends RuntimeException {

    public RateLimiterException(String message) {
        super(message);
    }
}
