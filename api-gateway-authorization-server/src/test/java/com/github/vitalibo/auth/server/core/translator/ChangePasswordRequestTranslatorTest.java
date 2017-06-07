package com.github.vitalibo.auth.server.core.translator;

import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.server.core.model.ChangePasswordRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ChangePasswordRequestTranslatorTest {

    @Test
    public void testTranslate() {
        ProxyRequest request = new ProxyRequest();
        request.setBody("username=admin&previous_password=Welcome2017!&proposed_password=Aq1Sw2De3");

        ChangePasswordRequest actual = ChangePasswordRequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getUsername(), "admin");
        Assert.assertEquals(actual.getPreviousPassword(), "Welcome2017!");
        Assert.assertEquals(actual.getProposedPassword(), "Aq1Sw2De3");
    }

    @Test
    public void testTranslateEmpty() {
        ProxyRequest request = new ProxyRequest();
        request.setBody("");

        ChangePasswordRequest actual = ChangePasswordRequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, new ChangePasswordRequest());
    }

}