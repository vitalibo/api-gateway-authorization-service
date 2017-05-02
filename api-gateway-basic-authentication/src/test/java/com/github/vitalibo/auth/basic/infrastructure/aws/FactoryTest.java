package com.github.vitalibo.auth.basic.infrastructure.aws;

import com.github.vitalibo.auth.basic.core.HttpBasicAuthenticator;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class FactoryTest {

    @Mock
    private AmazonFactory mockAmazonFactory;

    private Factory factory;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new Factory(mockAmazonFactory, Collections.emptyMap());
    }

    @Test
    public void testCreateHttpBasicAuthenticator() {
        HttpBasicAuthenticator actual = factory.createHttpBasicAuthenticator();

        Assert.assertNotNull(actual);
    }

}