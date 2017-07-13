package com.github.vitalibo.authorization.basic.infrastructure.aws.lambda;

import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.basic.core.HttpBasicAuthenticator;
import com.github.vitalibo.authorization.basic.core.Principal;
import com.github.vitalibo.authorization.basic.infrastructure.aws.Factory;
import com.github.vitalibo.authorization.shared.core.http.BasicAuthenticationException;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.AuthorizerRequest;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.AuthorizerResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

public class AuthorizerRequestHandlerTest {

    @Mock
    private Factory mockFactory;
    @Mock
    private HttpBasicAuthenticator mockHttpBasicAuthenticator;
    @Mock
    private Context mockContext;

    private AuthorizerRequestHandler lambda;
    private AuthorizerRequest request;
    private Principal principal;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockFactory.createHttpBasicAuthenticator()).thenReturn(mockHttpBasicAuthenticator);
        lambda = new AuthorizerRequestHandler(mockFactory);
        request = makeRequest();
        principal = makePrincipal();
    }

    @Test
    public void testAuthentication() {
        Mockito.when(mockHttpBasicAuthenticator.authenticate(Mockito.any()))
            .thenReturn(principal);

        AuthorizerResponse actual = lambda.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getPrincipalId(), principal.getId());
        Assert.assertTrue(Jackson.toJsonString(actual.getPolicyDocument()).contains("Allow"));
        Assert.assertEquals(actual.getContext().get("username"), principal.getUsername());
        Assert.assertEquals(actual.getContext().get("scope"), "foo,bar");
        Assert.assertEquals(actual.getContext().get("expirationTime"), 1234567890L);
    }

    @Test
    public void testUnauthorized() {
        Mockito.when(mockHttpBasicAuthenticator.authenticate(Mockito.any()))
            .thenThrow(NotAuthorizedException.class);

        AuthorizerResponse actual = lambda.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getPrincipalId(), null);
        Assert.assertTrue(Jackson.toJsonString(actual.getPolicyDocument()).contains("Deny"));
        Assert.assertEquals(actual.getContext().get("username"), null);
        Assert.assertEquals(actual.getContext().get("scope"), null);
        Assert.assertEquals(actual.getContext().get("expirationTime"), null);
    }

    @Test
    public void testValidationError() {
        Mockito.when(mockHttpBasicAuthenticator.authenticate(Mockito.any()))
            .thenThrow(BasicAuthenticationException.class);

        AuthorizerResponse actual = lambda.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getPrincipalId(), null);
        Assert.assertTrue(Jackson.toJsonString(actual.getPolicyDocument()).contains("Deny"));
        Assert.assertEquals(actual.getContext().get("username"), null);
        Assert.assertEquals(actual.getContext().get("scope"), null);
        Assert.assertEquals(actual.getContext().get("expirationTime"), null);
    }

    @Test(expectedExceptions = Exception.class)
    public void testInternalServerError() {
        Mockito.when(mockHttpBasicAuthenticator.authenticate(Mockito.any()))
            .thenThrow(Exception.class);

        lambda.handleRequest(request, mockContext);
    }

    private static AuthorizerRequest makeRequest() {
        AuthorizerRequest request = new AuthorizerRequest();
        request.setMethodArn("arn:aws:::*");
        request.setAuthorizationToken("Basic dXNlcjpwYXNzd29yZA==");
        return request;
    }

    private static Principal makePrincipal() {
        Principal principal = new Principal();
        principal.setId("32944624-1f4a-4f34-bdf6-5450679ef1bf");
        principal.setId("admin");
        principal.setScope(Arrays.asList("foo", "bar"));
        principal.setExpirationTime(1234567890L);
        return principal;
    }

}