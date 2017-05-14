package com.github.vitalibo.auth.server.core.facade;

import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.core.Rule;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyResponse;
import com.github.vitalibo.auth.server.core.Facade;
import com.github.vitalibo.auth.server.core.UserPool;
import com.github.vitalibo.auth.server.core.model.ChangePasswordRequest;
import com.github.vitalibo.auth.server.core.model.ChangePasswordResponse;
import com.github.vitalibo.auth.server.core.translator.ChangePasswordRequestTranslator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Collection;

public class ChangePasswordFacade implements Facade {

    private final UserPool userPool;
    private final Template template;
    private final ErrorState errorState;
    private final Collection<Rule<ProxyRequest>> preRules;
    private final Collection<Rule<ChangePasswordRequest>> postRules;

    public ChangePasswordFacade(UserPool userPool,
                                VelocityEngine velocityEngine,
                                ErrorState errorState,
                                Collection<Rule<ProxyRequest>> preRules,
                                Collection<Rule<ChangePasswordRequest>> postRules) {
        this.userPool = userPool;
        this.template = velocityEngine.getTemplate("index.html");
        this.errorState = errorState;
        this.preRules = preRules;
        this.postRules = postRules;
    }

    @Override
    public ProxyResponse process(ProxyRequest request) {
        if ("POST".equalsIgnoreCase(request.getHttpMethod())) {
            preRules.forEach(rule -> rule.accept(request, errorState));
            if (errorState.hasErrors()) {
                throw errorState;
            }

            return process(ChangePasswordRequestTranslator.from(request));
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

    private ProxyResponse process(ChangePasswordRequest request) {
        postRules.forEach(rule -> rule.accept(request, errorState));
        if (errorState.hasErrors()) {
            throw errorState;
        }

        boolean acknowledged = userPool.changePassword(
            request.getUsername(),
            request.getPreviousPassword(),
            request.getProposedPassword());

        ChangePasswordResponse response = new ChangePasswordResponse();
        response.setAcknowledged(acknowledged);
        response.setMessage(acknowledged ?
            "Your password has been changed successfully!" :
            "Incorrect Username or Password");

        return new ProxyResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withBody(response)
            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();
    }

}