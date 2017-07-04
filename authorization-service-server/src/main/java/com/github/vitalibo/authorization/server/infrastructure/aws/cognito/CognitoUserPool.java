package com.github.vitalibo.authorization.server.infrastructure.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.github.vitalibo.authorization.server.core.UserPool;
import com.github.vitalibo.authorization.server.core.UserPoolException;
import com.github.vitalibo.authorization.shared.core.Principal;
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
    public Principal authenticate(String username, String password) throws UserPoolException {
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

            Principal principal = new Principal();
            principal.setUsername(username);
            principal.setSession(authResult.getSession());
            principal.setAccessToken(Optional.ofNullable(authResult.getAuthenticationResult())
                .map(AuthenticationResultType::getIdToken).orElse(null));
            return principal;
        } catch (NotAuthorizedException | UserNotFoundException e) {
            throw new UserPoolException(e.getErrorMessage(), e);
        }
    }

    @Override
    public boolean changePassword(Principal principal, String newPassword) throws UserPoolException {
        logger.info("force change password challenge {}", principal.getUsername());

        try {
            RespondToAuthChallengeResult respondToAuthChallengeResult =
                identityProvider.respondToAuthChallenge(
                    new RespondToAuthChallengeRequest()
                        .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                        .withClientId(clientId)
                        .withSession(principal.getSession())
                        .withChallengeResponses(new HashMap<String, String>() {{
                            this.put("USERNAME", principal.getUsername());
                            this.put("NEW_PASSWORD", newPassword);
                        }}));

            return respondToAuthChallengeResult.getAuthenticationResult() != null;
        } catch (InvalidPasswordException e) {
            throw new UserPoolException(e.getErrorMessage(), e);
        }
    }

}
