package com.github.vitalibo.auth.basic.infrastructure.aws.lambda;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.github.vitalibo.auth.basic.core.HttpBasicAuthenticator;
import com.github.vitalibo.auth.basic.infrastructure.aws.Factory;
import com.github.vitalibo.auth.core.Principal;
import com.github.vitalibo.auth.infrastructure.aws.gateway.AuthorizerRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.AuthorizerResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class AuthorizerRequestHandler implements RequestHandler<AuthorizerRequest, AuthorizerResponse> {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizerRequestHandler.class);

    private final Factory factory;

    public AuthorizerRequestHandler() {
        this(Factory.getInstance());
    }

    @Override
    public AuthorizerResponse handleRequest(AuthorizerRequest request, Context context) {
        Statement.Effect effect = Statement.Effect.Deny;
        Principal principal = new Principal();
        HttpBasicAuthenticator authenticator = factory.createHttpBasicAuthenticator();

        try {
            principal = authenticator.auth(request.getAuthorizationToken());

            effect = Statement.Effect.Allow;
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error. {}", e.getMessage());
        } catch (UserNotFoundException | NotAuthorizedException e) {
            logger.warn("AWS Cognito error. {}", e.getErrorMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        return new AuthorizerResponse.Builder()
            .withPrincipalId(principal.getId())
            .withPolicyDocument(new Policy()
                .withStatements(new Statement(effect)
                    .withActions(() -> "execute-api:Invoke")
                    .withResources(new Resource(request.getMethodArn()))))
            .withContextAsString("username", principal.getUsername())
            .build();
    }

}
