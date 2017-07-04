package com.github.vitalibo.authorization.shared.core.validation;

import com.github.vitalibo.authorization.shared.core.AuthorizationServiceException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidationException extends AuthorizationServiceException {

    @Getter
    private final ErrorState errorState;

}
