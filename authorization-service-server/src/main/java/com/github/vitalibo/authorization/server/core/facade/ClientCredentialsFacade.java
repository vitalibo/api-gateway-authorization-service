package com.github.vitalibo.authorization.server.core.facade;

import com.github.vitalibo.authorization.server.core.Facade;
import com.github.vitalibo.authorization.server.core.UserIdentity;
import com.github.vitalibo.authorization.server.core.UserPool;
import com.github.vitalibo.authorization.server.core.UserPoolException;
import com.github.vitalibo.authorization.server.core.model.ClientCredentialsRequest;
import com.github.vitalibo.authorization.server.core.model.ClientCredentialsResponse;
import com.github.vitalibo.authorization.server.core.translator.ClientCredentialsRequestTranslator;
import com.github.vitalibo.authorization.shared.core.validation.ErrorState;
import com.github.vitalibo.authorization.shared.core.validation.Rule;
import com.github.vitalibo.authorization.shared.core.validation.ValidationException;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyError;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

@RequiredArgsConstructor
public class ClientCredentialsFacade implements Facade {

    private final ErrorState errorState;
    private final UserPool userPool;
    private final Collection<Rule<ProxyRequest>> preRules;
    private final Collection<Rule<ClientCredentialsRequest>> postRules;

    @Override
    public ProxyResponse process(ProxyRequest request) {
        preRules.forEach(rule -> rule.accept(request, errorState));
        if (errorState.hasErrors()) {
            throw new ValidationException(errorState);
        }

        try {
            ClientCredentialsResponse response = process(
                ClientCredentialsRequestTranslator.from(request));

            return new ProxyResponse.Builder()
                .withStatusCode(HttpStatus.SC_OK)
                .withBody(response)
                .build();

        } catch (UserPoolException e) {
            ErrorState errorState = new ErrorState();
            errorState.addError("authorization", e.getMessage());
            return new ProxyError.Builder()
                .withStatusCode(HttpStatus.SC_UNAUTHORIZED)
                .withErrorState(errorState)
                .build();
        }
    }

    ClientCredentialsResponse process(ClientCredentialsRequest request) throws UserPoolException {
        postRules.forEach(rule -> rule.accept(request, errorState));
        if (errorState.hasErrors()) {
            throw new ValidationException(errorState);
        }

        UserIdentity identity = userPool.authenticate(
            request.getClientId(), request.getClientSecret());

        ClientCredentialsResponse response = new ClientCredentialsResponse();
        response.setTokenType("Bearer");
        response.setAccessToken(identity.getAccessToken());
        response.setExpiresIn(
            ZonedDateTime.now(ZoneId.of("UTC")).plusHours(1)
                .toEpochSecond());
        return response;
    }

}
