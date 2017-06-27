package com.github.vitalibo.authorization.jwt.core;

import com.github.vitalibo.authorization.shared.core.AuthorizationServiceException;

public class JwtVerificationException extends AuthorizationServiceException {

    public JwtVerificationException(String message) {
        super(message);
    }

    public JwtVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

}