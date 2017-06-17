package com.github.vitalibo.authorization.jwt.infrastructure.aws.iam;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.github.vitalibo.authorization.jwt.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class PolicyCollectorTest {

    @Test
    public void testJoinPolicy() {
        List<Policy> policies = Arrays.asList(
            makePolicy(Statement.Effect.Allow, "arn:aws:execute-api:eu-west-1:12344567890:*/GET/*"),
            makePolicy(Statement.Effect.Deny, "arn:aws:execute-api:eu-west-1:0987654321:*/POST/*"));

        Policy actual = PolicyCollector.join(policies);

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