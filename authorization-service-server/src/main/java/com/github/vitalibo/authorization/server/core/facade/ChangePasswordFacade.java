package com.github.vitalibo.authorization.server.core.facade;

import com.github.vitalibo.authorization.server.core.Facade;
import com.github.vitalibo.authorization.server.core.UserPool;
import com.github.vitalibo.authorization.server.core.UserPoolException;
import com.github.vitalibo.authorization.server.core.model.ChangePasswordRequest;
import com.github.vitalibo.authorization.server.core.model.ChangePasswordResponse;
import com.github.vitalibo.authorization.server.core.translator.ChangePasswordRequestTranslator;
import com.github.vitalibo.authorization.shared.core.validation.ErrorState;
import com.github.vitalibo.authorization.shared.core.validation.Rule;
import com.github.vitalibo.authorization.shared.core.validation.ValidationException;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;
import java.util.Collection;

@RequiredArgsConstructor
public class ChangePasswordFacade implements Facade {

    private final ErrorState errorState;
    private final UserPool userPool;
    private final Template template;
    private final Collection<Rule<ProxyRequest>> preRules;
    private final Collection<Rule<ChangePasswordRequest>> postRules;

    @Override
    public ProxyResponse process(ProxyRequest request) {
        if ("POST".equals(request.getHttpMethod())) {
            preRules.forEach(rule -> rule.accept(request, errorState));
            if (errorState.hasErrors()) {
                throw new ValidationException(errorState);
            }

            ChangePasswordResponse response = process(
                ChangePasswordRequestTranslator.from(request));

            return new ProxyResponse.Builder()
                .withStatusCode(HttpStatus.SC_OK)
                .withBody(response)
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
        }

        VelocityContext context = new VelocityContext();
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return new ProxyResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withBody(writer.toString())
            .withHeader("Content-Type", "text/html; charset=utf-8")
            .build();
    }

    ChangePasswordResponse process(ChangePasswordRequest request) {
        postRules.forEach(rule -> rule.accept(request, errorState));
        if (errorState.hasErrors()) {
            throw new ValidationException(errorState);
        }

        ChangePasswordResponse response = new ChangePasswordResponse();
        response.setAcknowledged(false);

        try {
            userPool.changePassword(
                request.getUsername(), request.getPreviousPassword(), request.getProposedPassword());

            response.setAcknowledged(true);
            response.setMessage("Your password has been changed successfully!");
        } catch (UserPoolException e) {
            response.setMessage(e.getMessage());
        }

        return response;
    }

}
