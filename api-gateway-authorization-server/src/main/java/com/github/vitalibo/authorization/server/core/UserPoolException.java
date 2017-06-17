package com.github.vitalibo.authorization.server.core;

public class UserPoolException extends Exception {

    public UserPoolException() {
    }

    public UserPoolException(String message) {
        super(message);
    }

    public UserPoolException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserPoolException(Throwable cause) {
        super(cause);
    }
}
