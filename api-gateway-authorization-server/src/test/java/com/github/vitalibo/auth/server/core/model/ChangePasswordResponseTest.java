package com.github.vitalibo.auth.server.core.model;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.server.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ChangePasswordResponseTest {

    @Test
    public void testToJson() {
        ChangePasswordResponse response = new ChangePasswordResponse();
        response.setAcknowledged(true);
        response.setMessage("foo bar");

        String actual = Jackson.toJsonString(response);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, TestHelper.resourceAsJsonString("/ChangePasswordResponse.json"));
    }

}