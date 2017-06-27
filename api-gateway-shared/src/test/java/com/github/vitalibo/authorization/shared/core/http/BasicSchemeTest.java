package com.github.vitalibo.authorization.shared.core.http;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

public class BasicSchemeTest {

    @Test
    public void testDecode() {
        Credentials actual = BasicScheme.decode(
            header("Authorization", "Basic aHR0cHdhdGNoOmY="));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getUsername(), "httpwatch");
        Assert.assertEquals(actual.getPassword(), "f");
    }

    @DataProvider
    public Object[][] samplesEmptyHeader() {
        return new Object[][]{
            {header("foo", "bar")}, {header("Authorization", null)}, {header("Authorization", "")}
        };
    }

    @Test(dataProvider = "samplesEmptyHeader",
        expectedExceptions = BasicAuthenticationException.class,
        expectedExceptionsMessageRegExp = "The authorization header can't be empty.")
    public void testEmptyHeader(Map<String, String> headers) {
        BasicScheme.decode(headers);
    }

    @DataProvider
    public Object[][] samplesIncorrectHeader() {
        return new Object[][]{
            {"Basic"}, {"Basic aHR0cHdhdGNoOmY"}, {"aHR0cHdhdGNoOmY="}, {"Bearer aHR0cHdhdGNoOmY="}
        };
    }

    @Test(dataProvider = "samplesIncorrectHeader",
        expectedExceptions = BasicAuthenticationException.class,
        expectedExceptionsMessageRegExp = "Incorrect value of Basic Authentication header.")
    public void testIncorrectHeader(String header) {
        BasicScheme.decode(Collections.singletonMap("Authorization", header));
    }

    @DataProvider
    public Object[][] samplesIncorrectScheme() {
        return new Object[][]{
            {"Basic YWFhOmJiYjpjY2M="}, {"Basic Og=="}, {"Basic YTo="}, {"Basic OmE="}
        };
    }

    @Test(dataProvider = "samplesIncorrectScheme",
        expectedExceptions = BasicAuthenticationException.class,
        expectedExceptionsMessageRegExp = "Encrypted username and password has incorrect scheme.")
    public void testIncorrectScheme(String header) {
        BasicScheme.decode(Collections.singletonMap("Authorization", header));
    }

    private static Map<String, String> header(String key, String value) {
        return Collections.singletonMap(key, value);
    }

}