package com.github.vitalibo.auth.server.core.facade;

import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.core.Principal;
import com.github.vitalibo.auth.core.Rule;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyResponse;
import com.github.vitalibo.auth.server.core.Facade;
import com.github.vitalibo.auth.server.core.UserPool;
import com.github.vitalibo.auth.server.core.model.OAuth2Request;
import com.github.vitalibo.auth.server.core.model.OAuth2Response;
import com.github.vitalibo.auth.server.core.translator.OAuth2RequestTranslator;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;

import java.util.Collection;

@RequiredArgsConstructor
public class OAuth2ClientCredentialsFacade implements Facade {

    private final ErrorState errorState;
    private final UserPool userPool;
    private final Collection<Rule<ProxyRequest>> preRules;
    private final Collection<Rule<OAuth2Request>> postRules;

    @Override
    public ProxyResponse process(ProxyRequest request) {
        preRules.forEach(rule -> rule.accept(request, errorState));
        if (errorState.hasErrors()) {
            throw errorState;
        }

        OAuth2Response response = process(
            OAuth2RequestTranslator.from(request));

        return new ProxyResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withBody(response)
            .build();
    }

    private OAuth2Response process(OAuth2Request request) {
        postRules.forEach(rule -> rule.accept(request, errorState));
        if (errorState.hasErrors()) {
            throw errorState;
        }

        Principal principal = userPool.auth(
            request.getClientId(), request.getClientSecret());

        return new OAuth2Response();
    }

}
