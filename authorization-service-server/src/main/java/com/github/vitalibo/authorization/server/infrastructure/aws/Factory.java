package com.github.vitalibo.authorization.server.infrastructure.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import com.github.vitalibo.authorization.server.core.UserPool;
import com.github.vitalibo.authorization.server.core.ValidationRules;
import com.github.vitalibo.authorization.server.core.facade.ChangePasswordFacade;
import com.github.vitalibo.authorization.server.core.facade.ClientCredentialsFacade;
import com.github.vitalibo.authorization.server.infrastructure.aws.cognito.CognitoUserPool;
import com.github.vitalibo.authorization.shared.core.validation.ErrorState;
import lombok.Getter;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.util.Arrays;
import java.util.Map;

public class Factory {

    private static final String AWS_REGION = "AWS_REGION";
    private static final String AWS_COGNITO_USER_POOL_ID = "AWS_COGNITO_USER_POOL_ID";
    private static final String AWS_COGNITO_CLIENT_ID = "AWS_COGNITO_CLIENT_ID";

    @Getter(lazy = true)
    private static final Factory instance = new Factory(System.getenv());

    private final Regions awsRegion;
    private final UserPool userPool;
    private final VelocityEngine velocityEngine;

    Factory(Map<String, String> env) {
        awsRegion = Regions.fromName(env.get(AWS_REGION));
        userPool = createCognitoUserPool(
            env.get(AWS_COGNITO_USER_POOL_ID),
            env.get(AWS_COGNITO_CLIENT_ID));
        velocityEngine = createVelocityEngine();
    }

    public ClientCredentialsFacade createClientCredentialsFacade() {
        return new ClientCredentialsFacade(
            new ErrorState(),
            userPool,
            Arrays.asList(
                ValidationRules::verifyBody,
                ValidationRules::verifyBasicAuthenticationHeader),
            Arrays.asList(
                ValidationRules::verifyGrantType,
                ValidationRules::verifyClientId,
                ValidationRules::verifyClientSecret));
    }

    public ChangePasswordFacade createChangePasswordFacade() {
        return new ChangePasswordFacade(
            new ErrorState(),
            userPool,
            velocityEngine.getTemplate("public/index.html"),
            Arrays.asList(
                ValidationRules::verifyBody,
                ValidationRules::verifyBasicAuthenticationHeader),
            Arrays.asList(
                ValidationRules::verifyUserName,
                ValidationRules::verifyPreviousPassword,
                ValidationRules::verifyProposedPassword));
    }

    private UserPool createCognitoUserPool(String userPoolId, String clientId) {
        return new CognitoUserPool(
            AWSCognitoIdentityProviderClient.builder()
                .withRegion(awsRegion)
                .build(),
            userPoolId, clientId);
    }

    private static VelocityEngine createVelocityEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.setProperty("resource.loader", "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        return ve;
    }

}
