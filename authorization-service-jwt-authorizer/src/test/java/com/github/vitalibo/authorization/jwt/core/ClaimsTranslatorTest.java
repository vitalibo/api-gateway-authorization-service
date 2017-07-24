package com.github.vitalibo.authorization.jwt.core;

import com.nimbusds.jwt.JWTClaimsSet;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;

public class ClaimsTranslatorTest {

    @Test
    public void testFrom() throws ParseException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .claim("cognito:username", "admin")
            .claim("cognito:roles", Arrays.asList("foo", "bar"))
            .claim("exp", new Date(1234567890000L))
            .build();

        Claims actual = ClaimsTranslator.from(claimsSet);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getUsername(), "admin");
        Assert.assertEquals(actual.getRoles(), Arrays.asList("foo", "bar"));
        Assert.assertEquals(actual.getExpiredAt(), ZonedDateTime.of(2009, 2, 13, 23, 31, 30, 0, ZoneId.of("UTC")));
    }

    @Test
    public void testEmpty() throws ParseException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .claim("exp", new Date(1234567890000L))
            .build();

        Claims actual = ClaimsTranslator.from(claimsSet);
        Assert.assertNotNull(actual);
        Assert.assertNull(actual.getUsername());
        Assert.assertNull(actual.getRoles());
    }

}