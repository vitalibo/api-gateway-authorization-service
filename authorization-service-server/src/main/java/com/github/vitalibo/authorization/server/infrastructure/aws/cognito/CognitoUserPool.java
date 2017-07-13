package com.github.vitalibo.authorization.server.infrastructure.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.github.vitalibo.authorization.server.core.UserIdentity;
import com.github.vitalibo.authorization.server.core.UserPool;
import com.github.vitalibo.authorization.server.core.UserPoolException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
public class CognitoUserPool implements UserPool {

    private static final Logger logger = LoggerFactory.getLogger(CognitoUserPool.class);

    private final AWSCognitoIdentityProvider identityProvider;

    private final String userPoolId;
    private final String clientId;

    @Override
    public UserIdentity authenticate(String username, String password) throws UserPoolException {
        logger.info("authenticate user {}", username);

        try {
            AdminInitiateAuthResult authResult = identityProvider.adminInitiateAuth(
                new AdminInitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .withClientId(clientId)
                    .withUserPoolId(userPoolId)
                    .withAuthParameters(new HashMap<String, String>() {{
                        this.put("USERNAME", username);
                        this.put("PASSWORD", password);
                    }}));

            UserIdentity identity = new UserIdentity();
            identity.setUsername(username);
            identity.setSession(authResult.getSession());
            identity.setAccessToken(Optional.ofNullable(authResult.getAuthenticationResult())
                .map(AuthenticationResultType::getIdToken).orElse(null));
            return identity;
        } catch (NotAuthorizedException | UserNotFoundException e) {
            throw new UserPoolException(e.getErrorMessage(), e);
        }
    }

    @Override
    public boolean changePassword(UserIdentity identity, String newPassword) throws UserPoolException {
        logger.info("force change password challenge {}", identity.getUsername());

        try {
            RespondToAuthChallengeResult respondToAuthChallengeResult =
                identityProvider.respondToAuthChallenge(
                    new RespondToAuthChallengeRequest()
                        .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                        .withClientId(clientId)
                        .withSession(identity.getSession())
                        .withChallengeResponses(new HashMap<String, String>() {{
                            this.put("USERNAME", identity.getUsername());
                            this.put("NEW_PASSWORD", newPassword);
                        }}));

            return respondToAuthChallengeResult.getAuthenticationResult() != null;
        } catch (InvalidPasswordException e) {
            throw new UserPoolException(e.getErrorMessage(), e);
        }
    }

}
