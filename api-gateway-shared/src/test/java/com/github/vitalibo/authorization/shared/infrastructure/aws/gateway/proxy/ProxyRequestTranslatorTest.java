package com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.shared.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class ProxyRequestTranslatorTest {

    @Test
    public void testFrom() {
        ProxyRequest request = Jackson.fromJsonString(
            TestHelper.resourceAsString("/ApiGatewayProxyRequest.json"),
            ProxyRequest.class);

        ProxyRequest actual = ProxyRequestTranslator.ofNullable(request);

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
        Assert.assertTrue(actual.getIsBase64Encoded());
    }

    @Test
    public void testNotNull() {
        ProxyRequest request = Jackson.fromJsonString(
            "{}", ProxyRequest.class);

        ProxyRequest actual = ProxyRequestTranslator.ofNullable(request);

        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getResource());
        Assert.assertNotNull(actual.getPath());
        Assert.assertNotNull(actual.getHttpMethod());
        Assert.assertNotNull(actual.getHeaders());
        Assert.assertNotNull(actual.getQueryStringParameters());
        Assert.assertNotNull(actual.getPathParameters());
        Assert.assertNotNull(actual.getStageVariables());
        Assert.assertNotNull(actual.getRequestContext());
        Assert.assertNotNull(actual.getBody());
        Assert.assertNotNull(actual.getIsBase64Encoded());
    }

}