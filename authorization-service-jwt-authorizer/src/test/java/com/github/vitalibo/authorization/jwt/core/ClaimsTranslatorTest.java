package com.github.vitalibo.authorization.jwt.core;

import com.nimbusds.jwt.JWTClaimsSet;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.Arrays;

public class ClaimsTranslatorTest {

    @Test
    public void testFrom() throws ParseException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .claim("cognito:username", "admin")
            .claim("cognito:roles", Arrays.asList("foo", "bar"))
            .build();

        Claims actual = ClaimsTranslator.from(claimsSet);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getUsername(), "admin");
        Assert.assertEquals(actual.getRoles(), Arrays.asList("foo", "bar"));
    }

    @Test
    public void testEmpty() throws ParseException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .build();

        Claims actual = ClaimsTranslator.from(claimsSet);
        Assert.assertNotNull(actual);
        Assert.assertNull(actual.getUsername());
        Assert.assertNull(actual.getRoles());
    }

}