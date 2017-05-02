package com.github.vitalibo.auth.basic.core;

import com.github.vitalibo.auth.core.Principal;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class PrincipalTranslatorTest {

    @Test
    public void testFrom() {
        String sample = new BufferedReader(new InputStreamReader(
            PrincipalTranslatorTest.class.getResourceAsStream("/CognitoAccessKey.jwt")))
            .lines().collect(Collectors.joining());

        Principal actual = PrincipalTranslator.from(sample);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getId(), "1234567890");
        Assert.assertEquals(actual.getUsername(), "admin");
    }

}