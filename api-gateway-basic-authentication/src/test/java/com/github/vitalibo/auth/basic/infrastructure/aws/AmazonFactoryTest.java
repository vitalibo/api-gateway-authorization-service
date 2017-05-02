package com.github.vitalibo.auth.basic.infrastructure.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AmazonFactoryTest {

    @Mock
    private AWSCredentialsProvider mockCredentialsProvider;

    private AmazonFactory factory;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new AmazonFactory(Regions.DEFAULT_REGION, mockCredentialsProvider);
    }

    @Test
    public void testCreateAwsCognitoIdentityProvider() {
        AWSCognitoIdentityProvider actual = factory.getAwsCognitoIdentityProvider();

        Assert.assertNotNull(actual);
    }

}