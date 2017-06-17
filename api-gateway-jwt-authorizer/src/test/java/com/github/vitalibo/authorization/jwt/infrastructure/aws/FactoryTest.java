package com.github.vitalibo.authorization.jwt.infrastructure.aws;

import com.github.vitalibo.authorization.jwt.core.Jwt;
import com.github.vitalibo.authorization.jwt.core.PolicyRepository;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class FactoryTest {

    private Factory factory;

    @BeforeMethod
    public void setUp() {
        Map<String, String> env = new HashMap<>();
        env.put("AWS_REGION", "eu-west-1");
        env.put("AWS_COGNITO_USER_POOL_ID", "foo");
        factory = new Factory(env);
    }

    @Test
    public void testCreateJsonWebToken() {
        Jwt actual = factory.createJsonWebToken();

        Assert.assertNotNull(actual);
    }

    @Test
    public void testCreateRolePolicyRepository() {
        PolicyRepository actual = factory.createRolePolicyRepository();

        Assert.assertNotNull(actual);
    }

}