package com.github.vitalibo.auth.server.infrastructure.aws;

import com.github.vitalibo.auth.server.core.facade.ChangePasswordFacade;
import com.github.vitalibo.auth.server.core.facade.ClientCredentialsFacade;
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
        env.put("AWS_COGNITO_CLIENT_ID", "bar");
        factory = new Factory(env);
    }

    @Test
    public void testCreateClientCredentialsFacade() {
        ClientCredentialsFacade actual = factory.createClientCredentialsFacade();

        Assert.assertNotNull(actual);
    }

    @Test
    public void testCreateChangePasswordFacade() {
        ChangePasswordFacade actual = factory.createChangePasswordFacade();

        Assert.assertNotNull(actual);
    }

}