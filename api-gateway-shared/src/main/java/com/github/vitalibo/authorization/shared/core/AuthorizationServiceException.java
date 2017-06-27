package com.github.vitalibo.authorization.shared.core;

public class AuthorizationServiceException extends RuntimeException {

    public AuthorizationServiceException() {
        super();
    }

    public AuthorizationServiceException(String message) {
        super(message);
    }

    public AuthorizationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationServiceException(Throwable cause) {
        super(cause);
    }

}
