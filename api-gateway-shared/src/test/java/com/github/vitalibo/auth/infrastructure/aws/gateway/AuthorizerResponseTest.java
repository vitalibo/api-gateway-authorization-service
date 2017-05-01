package com.github.vitalibo.auth.infrastructure.aws.gateway;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthorizerResponseTest {

    @Test
    public void testBuild() {
        AuthorizerResponse response = new AuthorizerResponse.Builder()
            .withPrincipalId("32944624-1f4a-4f34-bdf6-5450679ef1bf")
            .withPolicyDocument(new Policy()
                .withStatements(new Statement(Statement.Effect.Allow)
                    .withActions(() -> "execute-api:Invoke")
                    .withResources(new Resource("arn:aws:execute-api:eu-west-1:1234567890:qwerty12345/v1/GET/resource/*"))))
            .withContextAsString("stringKey", "value")
            .withContextAsNumber("numberKey", 1)
            .withContextAsNumber("doubleKey", 1.1)
            .withContextAsBoolean("booleanKey", true)
            .build();

        Assert.assertNotNull(response);
        Assert.assertEquals(
            Jackson.toJsonString(response),
            TestHelper.resourceAsJsonString("/ApiGatewayAuthorizerResponse.json"));
    }

}