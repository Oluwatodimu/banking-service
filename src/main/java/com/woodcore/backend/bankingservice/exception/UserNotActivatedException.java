package com.woodcore.backend.bankingservice.exception;

import javax.naming.AuthenticationException;

public class UserNotActivatedException extends AuthenticationException {

    public UserNotActivatedException(String message) {
        super(message);
    }
}
