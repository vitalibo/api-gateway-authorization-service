package com.github.vitalibo.auth.server.core;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BasicAuthenticationHeaderTest {

    @Test
    public void testDecode() {
        BasicAuthenticationHeader actual = BasicAuthenticationHeader
            .decode("Basic aHR0cHdhdGNoOmY=");

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getUser(), "httpwatch");
        Assert.assertEquals(actual.getPassword(), "f");
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {null}, {""}, {"Basic aHR0cHdhdGNoOmY"}, {"Basic"}, {"aHR0cHdhdGNoOmY="},
            {"Base aHR0cHdhdGNoOmY="}, {"Basic YWFhOmJiYjpjY2M="}, {"Basic Og=="},
            {"Basic YTo="}, {"Basic OmE="}
        };
    }

    @Test(dataProvider = "samples", expectedExceptions = IllegalArgumentException.class)
    public void testFailDecode(String header) {
        BasicAuthenticationHeader.decode(header);
    }

}