package com.github.vitalibo.authorization.basic.infrastructure.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.github.vitalibo.authorization.basic.core.UserPool;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@RequiredArgsConstructor
public class CognitoUserPool implements UserPool {

    private static final Logger logger = LoggerFactory.getLogger(CognitoUserPool.class);

    private final String clientId;
    private final String userPoolId;

    private final AWSCognitoIdentityProvider identityProvider;

    @Override
    public String verify(String username, String password) {
        logger.info("verify user {}", username);

        AdminInitiateAuthResult authResult = identityProvider.adminInitiateAuth(
            new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withClientId(clientId)
                .withUserPoolId(userPoolId)
                .withAuthParameters(new HashMap<String, String>() {
                    {
                        this.put("USERNAME", username);
                        this.put("PASSWORD", password);
                    }
                }));

        AuthenticationResultType authenticationResult = authResult.getAuthenticationResult();

        return authenticationResult.getIdToken();
    }

}
