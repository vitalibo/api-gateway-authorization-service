package com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.shared.TestHelper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;


public class ProxyResponseTest {

    @Test
    public void testBuild() {
        ProxyResponse actual = new ProxyResponse.Builder()
            .withIsBase64Encoded(true)
            .withHeader(HttpHeaders.CACHE_CONTROL, "max-age=60")
            .withBody(Collections.singletonMap("message", "Hello World!"))
            .withStatusCode(HttpStatus.SC_OK)
            .build();

        Assert.assertNotNull(actual);
        Assert.assertEquals(
            Jackson.toJsonString(actual),
            TestHelper.resourceAsJsonString("/ApiGatewayProxyResponse.json"));
    }

    @Test
    public void testBuildPlaneText() {
        ProxyResponse actual = new ProxyResponse.Builder()
            .withBody("Plane text message")
            .withStatusCode(HttpStatus.SC_BAD_REQUEST)
            .build();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getIsBase64Encoded(), null);
        Assert.assertTrue(actual.getHeaders().isEmpty());
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_BAD_REQUEST);
        Assert.assertEquals(actual.getBody(), "Plane text message");
    }

}