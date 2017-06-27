package com.github.vitalibo.authorization.server.core;

import com.github.vitalibo.authorization.shared.core.AuthorizationServiceException;

public class UserPoolException extends AuthorizationServiceException {

    public UserPoolException() {
        super();
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
