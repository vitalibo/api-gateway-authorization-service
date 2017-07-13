package com.github.vitalibo.authorization.server.core.model;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.server.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ClientCredentialsRequestTest {

    @Test
    public void testFromJson() {
        ClientCredentialsRequest request = Jackson.fromJsonString(
            TestHelper.resourceAsString("/ClientCredentialsRequest.json"),
            ClientCredentialsRequest.class);

        Assert.assertNotNull(request);
        Assert.assertEquals(request.getGrantType(), "client_credentials");
        Assert.assertEquals(request.getClientId(), "1234567890");
        Assert.assertEquals(request.getClientSecret(), "zaq1xsw2cde3vfr4bgt5nhy6");
    }

}