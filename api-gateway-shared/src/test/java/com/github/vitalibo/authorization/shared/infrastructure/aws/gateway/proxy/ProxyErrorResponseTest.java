package com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.shared.TestHelper;
import com.github.vitalibo.authorization.shared.core.validation.ErrorState;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

public class ProxyErrorResponseTest {

    @Test
    public void testBadRequest() {
        ErrorState errorState = new ErrorState();
        errorState.put("key", Arrays.asList("foo", "bar"));
        ProxyErrorResponse error = new ProxyErrorResponse.Builder()
            .withStatusCode(HttpStatus.SC_BAD_REQUEST)
            .withErrorState(errorState)
            .withRequestId("aq1sw2de3fr4gt5hy6ju7ki8lo9p0")
            .build();

        Assert.assertNotNull(error);
        Assert.assertEquals(
            Jackson.toJsonString(error),
            TestHelper.resourceAsJsonString("/error/BadRequestResponse.json"));
    }

    @Test
    public void testNotFound() {
        ProxyErrorResponse error = new ProxyErrorResponse.Builder()
            .withStatusCode(HttpStatus.SC_NOT_FOUND)
            .withRequestId("aq1sw2de3fr4gt5hy6ju7ki8lo9p0")
            .build();

        Assert.assertNotNull(error);
        Assert.assertEquals(
            Jackson.toJsonString(error),
            TestHelper.resourceAsJsonString("/error/NotFoundResponse.json"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyError() {
        new ProxyErrorResponse.Builder().build();
    }

    @Test
    public void testAsProxyResponse() {
        ErrorState errorState = new ErrorState();
        errorState.put("key", Arrays.asList("foo", "bar"));
        ProxyResponse response = new ProxyErrorResponse.Builder()
            .withStatusCode(HttpStatus.SC_BAD_REQUEST)
            .withErrorState(errorState)
            .withRequestId("aq1sw2de3fr4gt5hy6ju7ki8lo9p0")
            .build()
            .asProxyResponse();

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatusCode(), (Integer) HttpStatus.SC_BAD_REQUEST);
        Assert.assertNotNull(response.getBody());
    }

}