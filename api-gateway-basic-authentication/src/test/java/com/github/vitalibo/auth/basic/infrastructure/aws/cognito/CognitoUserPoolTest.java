package com.github.vitalibo.auth.basic.infrastructure.aws.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CognitoUserPoolTest {

    @Mock
    private AWSCognitoIdentityProvider mockAwsCognitoIdentityProvider;
    @Mock
    private AdminInitiateAuthResult mockAdminInitiateAuthResult;
    @Mock
    private AuthenticationResultType mockAuthenticationResultType;

    private CognitoUserPool userPool;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userPool = new CognitoUserPool(
            "client_id", "user_pool_id", mockAwsCognitoIdentityProvider);
    }

    @Test
    public void testVerify() {
        Mockito.when(mockAwsCognitoIdentityProvider.adminInitiateAuth(Mockito.any()))
            .thenReturn(mockAdminInitiateAuthResult);
        Mockito.when(mockAdminInitiateAuthResult.getAuthenticationResult())
            .thenReturn(mockAuthenticationResultType);
        Mockito.when(mockAuthenticationResultType.getAccessToken())
            .thenReturn("access_token");
        String actual = userPool.verify("user_name", "password");

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "access_token");
    }

}