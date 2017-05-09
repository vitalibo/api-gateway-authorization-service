package com.github.vitalibo.auth.server.core;

import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.server.core.model.OAuth2Request;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

public class ValidationRulesTest {

    private ErrorState errorState;

    @BeforeMethod
    public void setUp() {
        errorState = new ErrorState();
    }

    @Test
    public void testPassVerifyBody() {
        ProxyRequest request = new ProxyRequest();
        request.setBody("{}");

        ValidationRules.verifyBody(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{{null}, {""}};
    }

    @Test(dataProvider = "samples")
    public void testFailVerifyBody(String body) {
        ProxyRequest request = new ProxyRequest();
        request.setBody(body);

        ValidationRules.verifyBody(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
        Assert.assertNotNull(errorState.get("body"));
    }

    @DataProvider
    public Object[][] samplesBasicAuthenticationHeader() {
        return new Object[][]{
            {null}, {""}, {"Basic aHR0cHdhdGNoOmY="}
        };
    }

    @Test(dataProvider = "samplesBasicAuthenticationHeader")
    public void testPassVerifyBasicAuthenticationHeader(String header) {
        ProxyRequest request = new ProxyRequest();
        request.setHeaders(Collections.singletonMap("Authorization", header));

        ValidationRules.verifyBasicAuthenticationHeader(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testFailVerifyBasicAuthenticationHeader() {
        ProxyRequest request = new ProxyRequest();
        request.setHeaders(Collections.singletonMap(
            "Authorization", "incorrect value"));

        ValidationRules.verifyBasicAuthenticationHeader(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
        Assert.assertNotNull(errorState.get("Authorization"));
    }

    @Test
    public void testPassVerifyGrantType() {
        OAuth2Request request = new OAuth2Request();
        request.setGrantType("client_credentials");

        ValidationRules.verifyGrantType(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @DataProvider
    public Object[][] samplesIncorrectGrantType() {
        return new Object[][]{
            {null}, {""}, {"refresh_token"}, {"incorrect value"}
        };
    }

    @Test(dataProvider = "samplesIncorrectGrantType")
    public void testFailVerifyGrantType(String grantType) {
        OAuth2Request request = new OAuth2Request();
        request.setGrantType(grantType);

        ValidationRules.verifyGrantType(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
        Assert.assertNotNull(errorState.get("grant_type"));
    }

    @Test
    public void testPassVerifyClientId() {
        OAuth2Request request = new OAuth2Request();
        request.setClientId("username");

        ValidationRules.verifyClientId(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test(dataProvider = "samples")
    public void testFailVerifyClientId(String clientId) {
        OAuth2Request request = new OAuth2Request();
        request.setClientId(clientId);

        ValidationRules.verifyClientId(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
        Assert.assertNotNull(errorState.get("client_id"));
    }

    @Test
    public void testPassVerifyClientSecret() {
        OAuth2Request request = new OAuth2Request();
        request.setClientSecret("secret");

        ValidationRules.verifyClientSecret(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test(dataProvider = "samples")
    public void testFailVerifyClientSecret(String clientSecret) {
        OAuth2Request request = new OAuth2Request();
        request.setClientSecret(clientSecret);

        ValidationRules.verifyClientSecret(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
        Assert.assertNotNull(errorState.get("client_secret"));
    }

}