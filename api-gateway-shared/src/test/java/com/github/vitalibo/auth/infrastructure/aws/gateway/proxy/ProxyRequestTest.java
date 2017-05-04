package com.github.vitalibo.auth.infrastructure.aws.gateway.proxy;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class ProxyRequestTest {

    @Test
    public void testMake() {
        ProxyRequest actual = Jackson.fromJsonString(
            TestHelper.resourceAsString("/ApiGatewayProxyRequest.json"),
            ProxyRequest.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getResource(), "/{proxy+}");
        Assert.assertEquals(actual.getPath(), "/hello/world");
        Assert.assertEquals(actual.getHttpMethod(), "POST");
        Map<String, String> headers = actual.getHeaders();
        Assert.assertNotNull(headers);
        Assert.assertEquals(headers.get("Content-Type"), "application/json");
        Map<String, String> queryStringParameters = actual.getQueryStringParameters();
        Assert.assertNotNull(queryStringParameters);
        Assert.assertEquals(queryStringParameters.get("name"), "me");
        Map<String, String> pathParameters = actual.getPathParameters();
        Assert.assertNotNull(pathParameters);
        Assert.assertEquals(pathParameters.get("proxy"), "hello/world");
        Map<String, String> stageVariables = actual.getStageVariables();
        Assert.assertNotNull(stageVariables);
        Assert.assertEquals(stageVariables.get("stageVariableName"), "stageVariableValue");
        Map<String, ?> requestContext = actual.getRequestContext();
        Assert.assertNotNull(requestContext);
        Assert.assertEquals(requestContext.get("accountId"), "12345678912");
        Map<String, String> identity = (Map<String, String>) requestContext.get("identity");
        Assert.assertNotNull(identity);
        Assert.assertEquals(identity.get("sourceIp"), "192.168.196.186");
        Assert.assertEquals(actual.getBody(), "{\r\n\t\"a\": 1\r\n}");
        Assert.assertFalse(actual.getIsBase64Encoded());
    }

}