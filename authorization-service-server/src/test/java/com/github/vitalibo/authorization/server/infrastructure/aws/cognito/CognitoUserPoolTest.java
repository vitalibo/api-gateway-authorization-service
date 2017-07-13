package com.github.vitalibo.authorization.server.infrastructure.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.github.vitalibo.authorization.server.core.UserPoolException;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CognitoUserPoolTest {

    @Mock
    private AWSCognitoIdentityProvider mockAwsCognitoIdentityProvider;
    @Mock
    private AdminInitiateAuthResult mockAdminInitiateAuthResult;
    @Mock
    private AuthenticationResultType mockAuthenticationResultType;
    @Captor
    private ArgumentCaptor<AdminInitiateAuthRequest> adminInitiateAuthRequestCaptor;
    @Captor
    private ArgumentCaptor<RespondToAuthChallengeRequest> respondToAuthChallengeRequestCaptor;
    @Captor
    private ArgumentCaptor<ChangePasswordRequest> changePasswordRequestCaptor;

    private CognitoUserPool cognitoUserPool;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cognitoUserPool = new CognitoUserPool(
            mockAwsCognitoIdentityProvider, "user_pool_id", "client_id");
    }

    @Test
    public void testAuthenticate() throws UserPoolException {
        Mockito.when(mockAwsCognitoIdentityProvider.adminInitiateAuth(Mockito.any()))
            .thenReturn(mockAdminInitiateAuthResult);
        Mockito.when(mockAdminInitiateAuthResult.getAuthenticationResult())
            .thenReturn(mockAuthenticationResultType);
        Mockito.when(mockAuthenticationResultType.getIdToken()).thenReturn("id_token");

        String actual = cognitoUserPool.authenticate("foo", "bar");

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "id_token");
        Mockito.verify(mockAwsCognitoIdentityProvider).adminInitiateAuth(adminInitiateAuthRequestCaptor.capture());
        AdminInitiateAuthRequest request = adminInitiateAuthRequestCaptor.getValue();
        Assert.assertEquals(request.getAuthFlow(), "ADMIN_NO_SRP_AUTH");
        Assert.assertEquals(request.getClientId(), "client_id");
        Assert.assertEquals(request.getUserPoolId(), "user_pool_id");
        Assert.assertEquals(request.getAuthParameters().get("USERNAME"), "foo");
        Assert.assertEquals(request.getAuthParameters().get("PASSWORD"), "bar");
    }

    @Test(expectedExceptions = UserPoolException.class, expectedExceptionsMessageRegExp = "New password required")
    public void testFailAuthenticate() throws UserPoolException {
        Mockito.when(mockAwsCognitoIdentityProvider.adminInitiateAuth(Mockito.any()))
            .thenReturn(mockAdminInitiateAuthResult);
        Mockito.when(mockAdminInitiateAuthResult.getChallengeName())
            .thenReturn(ChallengeNameType.NEW_PASSWORD_REQUIRED.name());

        cognitoUserPool.authenticate("foo", "bar");
    }

    @DataProvider
    public Object[][] samplesAuthenticateException() {
        return new Object[][]{
            {NotAuthorizedException.class}, {UserNotFoundException.class}
        };
    }

    @Test(dataProvider = "samplesAuthenticateException", expectedExceptions = UserPoolException.class)
    public void testFailAuthenticate(Class<Exception> e) throws UserPoolException {
        Mockito.when(mockAwsCognitoIdentityProvider.adminInitiateAuth(Mockito.any())).thenThrow(e);

        cognitoUserPool.authenticate("foo", "bar");
    }

    @Test
    public void testChangePassword() throws UserPoolException {
        Mockito.when(mockAwsCognitoIdentityProvider.adminInitiateAuth(Mockito.any()))
            .thenReturn(mockAdminInitiateAuthResult);
        Mockito.when(mockAdminInitiateAuthResult.getAuthenticationResult())
            .thenReturn(mockAuthenticationResultType);
        Mockito.when(mockAuthenticationResultType.getAccessToken())
            .thenReturn("access_token");

        cognitoUserPool.changePassword(
            "admin", "foo", "bar");

        Mockito.verify(mockAwsCognitoIdentityProvider).adminInitiateAuth(adminInitiateAuthRequestCaptor.capture());
        AdminInitiateAuthRequest authRequest = adminInitiateAuthRequestCaptor.getValue();
        Assert.assertEquals(authRequest.getAuthParameters().get("USERNAME"), "admin");
        Assert.assertEquals(authRequest.getAuthParameters().get("PASSWORD"), "foo");
        Mockito.verify(mockAwsCognitoIdentityProvider).changePassword(changePasswordRequestCaptor.capture());
        ChangePasswordRequest changePasswordRequest = changePasswordRequestCaptor.getValue();
        Assert.assertEquals(changePasswordRequest.getAccessToken(), "access_token");
        Assert.assertEquals(changePasswordRequest.getPreviousPassword(), "foo");
        Assert.assertEquals(changePasswordRequest.getProposedPassword(), "bar");
    }

    @Test
    public void testRespondToNewPasswordRequired() {
        Mockito.when(mockAwsCognitoIdentityProvider.adminInitiateAuth(Mockito.any()))
            .thenReturn(mockAdminInitiateAuthResult);
        Mockito.when(mockAdminInitiateAuthResult.getChallengeName())
            .thenReturn(ChallengeNameType.NEW_PASSWORD_REQUIRED.name());
        Mockito.when(mockAdminInitiateAuthResult.getSession())
            .thenReturn("session");

        cognitoUserPool.changePassword(
            "admin", "foo", "bar");

        Mockito.verify(mockAwsCognitoIdentityProvider).adminInitiateAuth(adminInitiateAuthRequestCaptor.capture());
        AdminInitiateAuthRequest authRequest = adminInitiateAuthRequestCaptor.getValue();
        Assert.assertEquals(authRequest.getAuthParameters().get("USERNAME"), "admin");
        Assert.assertEquals(authRequest.getAuthParameters().get("PASSWORD"), "foo");
        Mockito.verify(mockAwsCognitoIdentityProvider).respondToAuthChallenge(respondToAuthChallengeRequestCaptor.capture());
        RespondToAuthChallengeRequest respondToAuthChallengeRequest = respondToAuthChallengeRequestCaptor.getValue();
        Assert.assertEquals(respondToAuthChallengeRequest.getChallengeName(), "NEW_PASSWORD_REQUIRED");
        Assert.assertEquals(respondToAuthChallengeRequest.getClientId(), "client_id");
        Assert.assertEquals(respondToAuthChallengeRequest.getSession(), "session");
        Assert.assertEquals(respondToAuthChallengeRequest.getChallengeResponses().get("USERNAME"), "admin");
        Assert.assertEquals(respondToAuthChallengeRequest.getChallengeResponses().get("NEW_PASSWORD"), "bar");
    }

    @Test(expectedExceptions = UserPoolException.class)
    public void testFailRespondToNewPasswordRequired() throws UserPoolException {
        Mockito.when(mockAwsCognitoIdentityProvider.adminInitiateAuth(Mockito.any()))
            .thenReturn(mockAdminInitiateAuthResult);
        Mockito.when(mockAdminInitiateAuthResult.getChallengeName())
            .thenReturn(ChallengeNameType.NEW_PASSWORD_REQUIRED.name());
        Mockito.when(mockAwsCognitoIdentityProvider.respondToAuthChallenge(Mockito.any()))
            .thenThrow(InvalidPasswordException.class);

        cognitoUserPool.changePassword(
            "admin", "foo", "bar");
    }

    @Test(expectedExceptions = UserPoolException.class)
    public void testFailChangePassword() throws UserPoolException {
        Mockito.when(mockAwsCognitoIdentityProvider.adminInitiateAuth(Mockito.any()))
            .thenReturn(mockAdminInitiateAuthResult);
        Mockito.when(mockAdminInitiateAuthResult.getAuthenticationResult())
            .thenReturn(mockAuthenticationResultType);
        Mockito.when(mockAuthenticationResultType.getAccessToken())
            .thenReturn("access_token");
        Mockito.when(mockAwsCognitoIdentityProvider.changePassword(Mockito.any()))
            .thenThrow(InvalidPasswordException.class);

        cognitoUserPool.changePassword(
            "admin", "foo", "bar");
    }

}