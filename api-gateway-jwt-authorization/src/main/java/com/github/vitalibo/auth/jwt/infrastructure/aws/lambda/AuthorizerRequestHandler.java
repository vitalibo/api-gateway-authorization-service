package com.github.vitalibo.auth.jwt.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.github.vitalibo.auth.infrastructure.aws.gateway.AuthorizerRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.AuthorizerResponse;
import com.github.vitalibo.auth.jwt.core.Claims;
import com.github.vitalibo.auth.jwt.core.JWT;
import com.github.vitalibo.auth.jwt.infrastructure.aws.Factory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthorizerRequestHandler implements RequestHandler<AuthorizerRequest, AuthorizerResponse> {

    private final Factory factory;

    public AuthorizerRequestHandler() {
        this(Factory.getInstance());
    }

    @Override
    public AuthorizerResponse handleRequest(AuthorizerRequest request, Context context) {
        JWT jwt = factory.createJWT();
        Claims claims = jwt.verify(request.getAuthorizationToken());

        return new AuthorizerResponse.Builder()
            .build();
    }

}