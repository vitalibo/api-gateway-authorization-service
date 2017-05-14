package com.github.vitalibo.auth.server.core;

import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RouterTest {

    @DataProvider
    public Object[][] samplesNotFound() {
        return new Object[][]{
            {sample("/", "GET")},
            {sample("/oauth/token", "GET")},
            {sample("/oauth/tokens", "POST")},
            {sample("/account", "PUT")}
        };
    }

    @Test(dataProvider = "samplesNotFound")
    public void testMatchNotFound(ProxyRequest request) {
        Route actual = Router.match(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Route.NOT_FOUND);
    }

    @Test
    public void testMatchOAuth2Facade() {
        ProxyRequest request = sample("/oauth/token", "POST");

        Route actual = Router.match(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Route.OAUTH2_CLIENT_CREDENTIALS);
    }

    @DataProvider
    public Object[][] samplesHttpMethod() {
        return new Object[][]{
            {"GET"}, {"POST"}
        };
    }

    @Test(dataProvider = "samplesHttpMethod")
    public void testMatchChangePassword(String httpMethod) {
        ProxyRequest request = sample("/account", httpMethod);

        Route actual = Router.match(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Route.CHANGE_PASSWORD);
    }

    private static ProxyRequest sample(String path, String httpMethod) {
        ProxyRequest request = new ProxyRequest();
        request.setPath(path);
        request.setHttpMethod(httpMethod);
        return request;
    }

}