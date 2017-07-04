package com.github.vitalibo.authorization.shared.infrastructure.aws.gateway;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.shared.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthorizerRequestTest {

    @Test
    public void testFromJson() {
        AuthorizerRequest request = Jackson.fromJsonString(
            TestHelper.resourceAsString("/ApiGatewayAuthorizerRequest.json"),
            AuthorizerRequest.class);

        Assert.assertNotNull(request);
        Assert.assertEquals(request.getType(), "TOKEN");
        Assert.assertEquals(request.getAuthorizationToken(), "Basic dXNlcjoxMjM0");
        Assert.assertEquals(request.getMethodArn(), "arn:aws:execute-api:eu-west-1:1234567890:qwerty12345/v1/GET/resource/*");
    }

}