package com.github.vitalibo.auth.basic.infrastructure.aws;

import com.github.vitalibo.auth.basic.core.HttpBasicAuthenticator;
import com.github.vitalibo.auth.basic.core.UserPool;
import com.github.vitalibo.auth.basic.infrastructure.aws.cognito.CognitoUserPool;
import lombok.Getter;

import java.util.Map;

public class Factory {

    @Getter(lazy = true)
    private static final Factory instance = new Factory();

    private final AmazonFactory amazonFactory;
    private final String userPoolId;
    private final String clientId;

    private Factory() {
        this(new AmazonFactory(System.getenv("AWS_REGION")), System.getenv());
    }

    Factory(AmazonFactory amazonFactory, Map<String, String> conf) {
        this.amazonFactory = amazonFactory;
        this.userPoolId = conf.get("COGNITO_USER_POOL_ID");
        this.clientId = conf.get("COGNITO_CLIENT_ID");
    }

    public HttpBasicAuthenticator createHttpBasicAuthenticator() {
        return new HttpBasicAuthenticator(
            createCognitoUserPool());
    }

    private UserPool createCognitoUserPool() {
        return new CognitoUserPool(
            clientId, userPoolId,
            amazonFactory.getAwsCognitoIdentityProvider());
    }

}
