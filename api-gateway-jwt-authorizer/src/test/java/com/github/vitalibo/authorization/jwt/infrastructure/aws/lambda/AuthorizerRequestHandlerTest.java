package com.github.vitalibo.authorization.jwt.infrastructure.aws.lambda;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.jwt.core.AuthorizationException;
import com.github.vitalibo.authorization.jwt.core.Claims;
import com.github.vitalibo.authorization.jwt.core.Jwt;
import com.github.vitalibo.authorization.jwt.core.PolicyRepository;
import com.github.vitalibo.authorization.jwt.infrastructure.aws.Factory;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.AuthorizerRequest;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.AuthorizerResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class AuthorizerRequestHandlerTest {

    @Mock
    private Factory mockFactory;
    @Mock
    private Jwt mockJwt;
    @Mock
    private PolicyRepository mockPolicyRepository;
    @Mock
    private Context mockContext;

    private AuthorizerRequestHandler handler;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockFactory.createJsonWebToken()).thenReturn(mockJwt);
        Mockito.when(mockFactory.createRolePolicyRepository()).thenReturn(mockPolicyRepository);
        handler = new AuthorizerRequestHandler(mockFactory);
    }

    @Test
    public void testUnauthorizedRequest() {
        Mockito.when(mockJwt.verify(Mockito.anyString())).thenThrow(AuthorizationException.class);
        AuthorizerRequest request = makeAuthorizerRequest();

        AuthorizerResponse actual = handler.handleRequest(request, mockContext);

        Assert.assertNull(actual);
        Mockito.verify(mockJwt).verify(request.getAuthorizationToken());
        Mockito.verify(mockPolicyRepository, Mockito.never()).getPolicy(Mockito.any());
    }

    @Test
    public void testHandleRequest() {
        AuthorizerRequest request = makeAuthorizerRequest();
        Claims claims = makeClaims();
        Mockito.when(mockJwt.verify(Mockito.anyString())).thenReturn(claims);
        Policy policy = makePolicy(Statement.Effect.Allow);
        Mockito.when(mockPolicyRepository.getPolicy(claims)).thenReturn(policy);

        AuthorizerResponse actual = handler.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getPrincipalId(), claims.getUsername());
        Assert.assertEquals(Jackson.toJsonString(actual.getPolicyDocument()), policy.toJson());
        Assert.assertTrue(actual.getContext().isEmpty());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testInternalServerError() {
        Mockito.when(mockJwt.verify(Mockito.anyString())).thenReturn(makeClaims());
        Mockito.when(mockPolicyRepository.getPolicy(Mockito.any())).thenThrow(RuntimeException.class);

        handler.handleRequest(makeAuthorizerRequest(), mockContext);
    }

    private static AuthorizerRequest makeAuthorizerRequest() {
        AuthorizerRequest request = new AuthorizerRequest();
        request.setType("token");
        request.setMethodArn("api-gateway-arn");
        request.setAuthorizationToken("jwt");
        return request;
    }

    private static Claims makeClaims() {
        Claims claims = new Claims();
        claims.setRoles(Collections.singletonList("arn"));
        claims.setUsername("foo");
        return claims;
    }

    private static Policy makePolicy(Statement.Effect effect) {
        return new Policy()
            .withId("test-id")
            .withStatements(new Statement(effect)
                .withActions(() -> "execute-api:Invoke")
                .withResources(new Resource("arn:aws:execute-api:eu-west-1:12344567890:*")));
    }

}