package com.github.vitalibo.auth.server.core.facade;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.core.Principal;
import com.github.vitalibo.auth.core.Rule;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyResponse;
import com.github.vitalibo.auth.server.core.UserPool;
import com.github.vitalibo.auth.server.core.UserPoolException;
import com.github.vitalibo.auth.server.core.model.OAuth2Request;
import com.github.vitalibo.auth.server.core.model.OAuth2Response;
import org.apache.http.HttpStatus;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;

public class ClientCredentialsFacadeTest {

    @Mock
    private UserPool mockUserPool;
    @Spy
    private ErrorState spyErrorState;
    @Mock
    private Collection<Rule<ProxyRequest>> mockPreRules;
    @Mock
    private Collection<Rule<OAuth2Request>> mockPostRules;

    private ClientCredentialsFacade facade;

    @BeforeMethod
    public void setUp() throws UserPoolException {
        MockitoAnnotations.initMocks(this);
        facade = new ClientCredentialsFacade(
            spyErrorState, mockUserPool, mockPreRules, mockPostRules);
    }

    @Test
    public void testAuthenticate() throws UserPoolException {
        OAuth2Request request = makeOAuth2Request();
        Principal principal = new Principal();
        principal.setAccessToken("ACCESS_TOKEN");
        Mockito.when(mockUserPool.authenticate(request.getClientId(), request.getClientSecret()))
            .thenReturn(principal);

        OAuth2Response actual = facade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getAccessToken(), "ACCESS_TOKEN");
        Assert.assertEquals(actual.getTokenType(), "Bearer");
        Assert.assertTrue(Math.abs(actual.getExpiresIn() - Instant.now().getEpochSecond()) <= 3600);
    }

    @Test
    public void testAuthorized() throws UserPoolException {
        Mockito.when(mockUserPool.authenticate(Mockito.any(), Mockito.any()))
            .thenReturn(new Principal());
        ProxyRequest request = makeProxyRequest();

        ProxyResponse actual = facade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_OK);
        Assert.assertNotNull(actual.getBody());
    }

    @Test
    public void testUnauthorized() throws UserPoolException {
        Mockito.when(mockUserPool.authenticate("foo", "bar"))
            .thenThrow(UserPoolException.class);
        ProxyRequest request = makeProxyRequest();

        ProxyResponse actual = facade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_UNAUTHORIZED);
        Assert.assertNotNull(actual.getBody());
    }

    private static OAuth2Request makeOAuth2Request() {
        OAuth2Request request = new OAuth2Request();
        request.setGrantType("client_credentials");
        request.setClientId("foo");
        request.setClientSecret("bar");
        return request;
    }

    private static ProxyRequest makeProxyRequest() {
        ProxyRequest request = new ProxyRequest();
        request.setHeaders(new HashMap<>());
        request.setBody(Jackson.toJsonString(makeOAuth2Request()));
        return request;
    }

}