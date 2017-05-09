package com.github.vitalibo.auth.server.core.model;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.server.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OAuth2RequestTest {

    @Test
    public void testFromJson() {
        OAuth2Request request = Jackson.fromJsonString(
            TestHelper.resourceAsString("/OAuth2Request.json"),
            OAuth2Request.class);

        Assert.assertNotNull(request);
        Assert.assertEquals(request.getGrantType(), "client_credentials");
        Assert.assertEquals(request.getClientId(), "1234567890");
        Assert.assertEquals(request.getClientSecret(), "zaq1xsw2cde3vfr4bgt5nhy6");
    }

}