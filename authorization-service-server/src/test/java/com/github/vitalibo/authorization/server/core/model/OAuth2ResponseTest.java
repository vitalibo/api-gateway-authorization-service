package com.github.vitalibo.authorization.server.core.model;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.server.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OAuth2ResponseTest {

    @Test
    public void testToJson() {
        OAuth2Response response = new OAuth2Response();
        response.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        response.setExpiresIn(1234567890L);
        response.setTokenType("Bearer");

        String actual = Jackson.toJsonString(response);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, TestHelper.resourceAsJsonString("/OAuth2Response.json"));
    }

}