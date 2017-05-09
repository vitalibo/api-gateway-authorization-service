package com.github.vitalibo.auth.server.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyResponse;
import com.github.vitalibo.auth.server.core.facade.OAuth2ClientCredentialsFacade;
import com.github.vitalibo.auth.server.infrastructure.aws.Factory;
import org.apache.http.HttpStatus;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProxyRequestHandlerTest {

    @Mock
    private Factory mockFactory;
    @Mock
    private OAuth2ClientCredentialsFacade mockOAuth2ClientCredentialsFacade;
    @Mock
    private Context mockContext;

    private ProxyRequestHandler lambda;
    private ProxyRequest proxyRequest;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockFactory.createOAuth2ClientCredentialsFacade())
            .thenReturn(mockOAuth2ClientCredentialsFacade);
        lambda = new ProxyRequestHandler(mockFactory);
        proxyRequest = new ProxyRequest();
    }

    @Test
    public void testNotFound() {
        proxyRequest.setPath("/foo");
        proxyRequest.setHttpMethod("GET");

        ProxyResponse actual = lambda.handleRequest(proxyRequest, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_NOT_FOUND);
        Assert.assertTrue(actual.getBody().contains("Not Found"));
    }

    @Test
    public void testBadRequest() {
        proxyRequest.setPath("/oauth/token");
        proxyRequest.setHttpMethod("POST");
        ErrorState errorState = new ErrorState();
        errorState.addError("key", "error message");
        Mockito.when(mockOAuth2ClientCredentialsFacade.process(Mockito.any()))
            .thenThrow(errorState);

        ProxyResponse actual = lambda.handleRequest(proxyRequest, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_BAD_REQUEST);
        Assert.assertTrue(actual.getBody().contains("Bad Request"));
        Assert.assertTrue(actual.getBody().contains("error message"));
    }

    @Test(expectedExceptions = Exception.class)
    public void testInternalServerError() {
        proxyRequest.setPath("/oauth/token");
        proxyRequest.setHttpMethod("POST");
        Mockito.when(mockOAuth2ClientCredentialsFacade.process(Mockito.any()))
            .thenThrow(Exception.class);

        lambda.handleRequest(proxyRequest, mockContext);
    }

    @Test
    public void testInvokeOAuth2ClientCredentialsFacade() {
        proxyRequest.setPath("/oauth/token");
        proxyRequest.setHttpMethod("POST");
        ProxyResponse response = new ProxyResponse.Builder()
            .withStatusCode(HttpStatus.SC_OK)
            .withBody("OK")
            .build();

        Mockito.when(mockOAuth2ClientCredentialsFacade.process(Mockito.any()))
            .thenReturn(response);

        ProxyResponse actual = lambda.handleRequest(proxyRequest, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_OK);
        Assert.assertEquals(actual.getBody(), "OK");
        Mockito.verify(mockOAuth2ClientCredentialsFacade).process(Mockito.any());
    }

}