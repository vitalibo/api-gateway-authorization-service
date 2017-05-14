package com.github.vitalibo.auth.server.core.model;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.server.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ChangePasswordRequestTest {

    @Test
    public void testFromJson() {
        ChangePasswordRequest request = Jackson.fromJsonString(
            TestHelper.resourceAsString("/ChangePasswordRequest.json"),
            ChangePasswordRequest.class);

        Assert.assertNotNull(request);
        Assert.assertEquals(request.getUsername(), "admin");
        Assert.assertEquals(request.getPreviousPassword(), "welcome");
        Assert.assertEquals(request.getProposedPassword(), "s3cr3t");
    }

}