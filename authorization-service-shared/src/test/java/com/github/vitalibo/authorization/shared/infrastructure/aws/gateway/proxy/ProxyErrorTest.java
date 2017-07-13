package com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy;

import com.github.vitalibo.authorization.shared.TestHelper;
import com.github.vitalibo.authorization.shared.core.validation.ErrorState;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

public class ProxyErrorTest {

    @Test
    public void testBadRequest() {
        ErrorState errorState = new ErrorState();
        errorState.put("key", Arrays.asList("foo", "bar"));
        ProxyResponse error = new ProxyError.Builder()
            .withStatusCode(HttpStatus.SC_BAD_REQUEST)
            .withErrorState(errorState)
            .withRequestId("aq1sw2de3fr4gt5hy6ju7ki8lo9p0")
            .build();

        Assert.assertNotNull(error);
        Assert.assertEquals(
            error.getBody(),
            TestHelper.resourceAsJsonString("/error/BadRequestResponse.json"));
    }

    @Test
    public void testNotFound() {
        ProxyResponse error = new ProxyError.Builder()
            .withStatusCode(HttpStatus.SC_NOT_FOUND)
            .withRequestId("aq1sw2de3fr4gt5hy6ju7ki8lo9p0")
            .build();

        Assert.assertNotNull(error);
        Assert.assertEquals(
            error.getBody(),
            TestHelper.resourceAsJsonString("/error/NotFoundResponse.json"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyError() {
        new ProxyError.Builder()
            .build();
    }

    @Test
    public void testBuild() {
        ErrorState errorState = new ErrorState();
        errorState.put("key", Arrays.asList("foo", "bar"));
        ProxyResponse response = new ProxyError.Builder()
            .withStatusCode(HttpStatus.SC_BAD_REQUEST)
            .withErrorState(errorState)
            .withRequestId("aq1sw2de3fr4gt5hy6ju7ki8lo9p0")
            .build();

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatusCode(), (Integer) HttpStatus.SC_BAD_REQUEST);
        Assert.assertNotNull(response.getBody());
    }

}