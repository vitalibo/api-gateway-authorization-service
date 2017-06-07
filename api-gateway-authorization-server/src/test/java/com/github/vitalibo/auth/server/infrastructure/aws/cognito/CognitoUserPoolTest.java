package com.github.vitalibo.auth.server.infrastructure.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.github.vitalibo.auth.core.Principal;
import com.github.vitalibo.auth.server.core.UserPoolException;
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
    @Mock
    private RespondToAuthChallengeResult mockRespondToAuthChallengeResult;
    @Captor
    private ArgumentCaptor<RespondToAuthChallengeRequest> respondToAuthChallengeRequestCaptor;

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
        Mockito.when(mockAdminInitiateAuthResult.getSession()).thenReturn("session");
        Mockito.when(mockAuthenticationResultType.getIdToken()).thenReturn("id_token");

        Principal actual = cognitoUserPool.authenticate("foo", "bar");

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getUsername(), "foo");
        Assert.assertEquals(actual.getSession(), "session");
        Assert.assertEquals(actual.getAccessToken(), "id_token");
        Mockito.verify(mockAwsCognitoIdentityProvider).adminInitiateAuth(adminInitiateAuthRequestCaptor.capture());
        AdminInitiateAuthRequest request = adminInitiateAuthRequestCaptor.getValue();
        Assert.assertEquals(request.getAuthFlow(), "ADMIN_NO_SRP_AUTH");
        Assert.assertEquals(request.getClientId(), "client_id");
        Assert.assertEquals(request.getUserPoolId(), "user_pool_id");
        Assert.assertEquals(request.getAuthParameters().get("USERNAME"), "foo");
        Assert.assertEquals(request.getAuthParameters().get("PASSWORD"), "bar");
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
        Mockito.when(mockAwsCognitoIdentityProvider.respondToAuthChallenge(Mockito.any()))
            .thenReturn(mockRespondToAuthChallengeResult);
        Mockito.when(mockRespondToAuthChallengeResult.getAuthenticationResult())
            .thenReturn(mockAuthenticationResultType);

        boolean actual = cognitoUserPool.changePassword(makePrincipal(), "new_password");

        Assert.assertTrue(actual);
        Mockito.verify(mockAwsCognitoIdentityProvider).respondToAuthChallenge(respondToAuthChallengeRequestCaptor.capture());
        RespondToAuthChallengeRequest request = respondToAuthChallengeRequestCaptor.getValue();
        Assert.assertEquals(request.getChallengeName(), "NEW_PASSWORD_REQUIRED");
        Assert.assertEquals(request.getClientId(), "client_id");
        Assert.assertEquals(request.getSession(), "session");
        Assert.assertEquals(request.getChallengeResponses().get("USERNAME"), "foo");
        Assert.assertEquals(request.getChallengeResponses().get("NEW_PASSWORD"), "new_password");
    }

    @Test(expectedExceptions = UserPoolException.class)
    public void testFailChangePassword() throws UserPoolException {
        Mockito.when(mockAwsCognitoIdentityProvider.respondToAuthChallenge(Mockito.any()))
            .thenThrow(InvalidPasswordException.class);

        cognitoUserPool.changePassword(makePrincipal(), "foobar");
    }

    private static Principal makePrincipal() {
        Principal principal = new Principal();
        principal.setUsername("foo");
        principal.setSession("session");
        return principal;
    }

}