package com.github.vitalibo.authorization.shared.core.http;

import com.github.vitalibo.authorization.shared.core.AuthorizationServiceException;

public class BasicAuthenticationException extends AuthorizationServiceException {

    public BasicAuthenticationException() {
        super();
    }

    public BasicAuthenticationException(String message) {
        super(message);
    }

    public BasicAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BasicAuthenticationException(Throwable cause) {
        super(cause);
    }

}
