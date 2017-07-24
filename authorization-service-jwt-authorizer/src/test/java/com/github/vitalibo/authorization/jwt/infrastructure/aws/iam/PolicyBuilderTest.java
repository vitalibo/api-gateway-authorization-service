package com.github.vitalibo.authorization.jwt.infrastructure.aws.iam;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.github.vitalibo.authorization.jwt.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class PolicyBuilderTest {

    @Test
    public void testBuild() {
        List<Policy> policies = Arrays.asList(
            makePolicy(Statement.Effect.Allow, "arn:aws:execute-api:eu-west-1:12344567890:*/GET/*"),
            makePolicy(Statement.Effect.Deny, "arn:aws:execute-api:eu-west-1:0987654321:*/POST/*"));

        Policy actual = new PolicyBuilder()
            .withPolicies(policies)
            .withExpiredAt(ZonedDateTime.of(2009, 2, 13, 23, 31, 30, 0, ZoneId.of("UTC")))
            .build();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.toJson(), TestHelper.resourceAsJsonString("/Policy.json"));
    }

    private static Policy makePolicy(Statement.Effect effect, String resource) {
        return new Policy()
            .withId("test-id")
            .withStatements(new Statement(effect)
                .withActions(() -> "execute-api:Invoke")
                .withResources(new Resource(resource)));
    }

}