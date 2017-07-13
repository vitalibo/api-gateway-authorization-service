package com.github.vitalibo.authorization.server.infrastructure.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.github.vitalibo.authorization.server.core.UserPool;
import com.github.vitalibo.authorization.server.core.UserPoolException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@RequiredArgsConstructor
public class CognitoUserPool implements UserPool {

    private static final Logger logger = LoggerFactory.getLogger(CognitoUserPool.class);

    private final AWSCognitoIdentityProvider identityProvider;

    private final String userPoolId;
    private final String clientId;

    @Override
    public String authenticate(String username,
                               String password) throws UserPoolException {
        logger.info("authenticate user {}", username);

        try {
            AdminInitiateAuthResult adminInitiateAuthResult =
                adminInitiateAuth(username, password);
            if ("NEW_PASSWORD_REQUIRED".equals(adminInitiateAuthResult.getChallengeName())) {
                throw new UserPoolException("New password required");
            }

            return adminInitiateAuthResult.getAuthenticationResult()
                .getIdToken();
        } catch (NotAuthorizedException | UserNotFoundException e) {
            throw new UserPoolException(e.getErrorMessage(), e);
        }
    }

    @Override
    public void changePassword(String username,
                               String previousPassword,
                               String proposedPassword) throws UserPoolException {
        logger.info("force change password challenge {}", username);

        try {
            AdminInitiateAuthResult adminInitiateAuthResult =
                adminInitiateAuth(username, previousPassword);
            if ("NEW_PASSWORD_REQUIRED".equals(adminInitiateAuthResult.getChallengeName())) {
                respondToNewPasswordRequired(
                    adminInitiateAuthResult, username, proposedPassword);
                return;
            }

            changePassword(
                adminInitiateAuthResult.getAuthenticationResult(),
                previousPassword, proposedPassword);
        } catch (NotAuthorizedException | UserNotFoundException | InvalidPasswordException e) {
            throw new UserPoolException(e.getErrorMessage(), e);
        }
    }

    private AdminInitiateAuthResult adminInitiateAuth(String username, String password) {
        return identityProvider.adminInitiateAuth(
            new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withClientId(clientId)
                .withUserPoolId(userPoolId)
                .withAuthParameters(new HashMap<String, String>() {{
                    this.put("USERNAME", username);
                    this.put("PASSWORD", password);
                }}));
    }

    private RespondToAuthChallengeResult respondToNewPasswordRequired(AdminInitiateAuthResult adminInitiateAuthResult,
                                                                      String username, String newPassword) {
        return identityProvider.respondToAuthChallenge(
            new RespondToAuthChallengeRequest()
                .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .withClientId(clientId)
                .withSession(adminInitiateAuthResult.getSession())
                .withChallengeResponses(new HashMap<String, String>() {{
                    this.put("USERNAME", username);
                    this.put("NEW_PASSWORD", newPassword);
                }}));
    }

    private ChangePasswordResult changePassword(AuthenticationResultType authenticationResultType,
                                                String previousPassword, String proposedPassword) {
        return identityProvider.changePassword(
            new ChangePasswordRequest()
                .withAccessToken(authenticationResultType.getAccessToken())
                .withPreviousPassword(previousPassword)
                .withProposedPassword(proposedPassword));
    }

}
