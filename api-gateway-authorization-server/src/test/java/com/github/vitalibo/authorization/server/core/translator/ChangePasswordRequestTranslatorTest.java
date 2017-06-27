package com.github.vitalibo.authorization.server.core.translator;

import com.github.vitalibo.authorization.server.core.model.ChangePasswordRequest;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;
import org.apache.http.HttpHeaders;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;

public class ChangePasswordRequestTranslatorTest {

    @Test
    public void testTranslate() {
        ProxyRequest request = makeProxyRequest(
            "username=admin&previous_password=Welcome2017!&proposed_password=Aq1Sw2De3");

        ChangePasswordRequest actual = ChangePasswordRequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getUsername(), "admin");
        Assert.assertEquals(actual.getPreviousPassword(), "Welcome2017!");
        Assert.assertEquals(actual.getProposedPassword(), "Aq1Sw2De3");
    }

    @Test
    public void testTranslateEmpty() {
        ProxyRequest request = makeProxyRequest("");

        ChangePasswordRequest actual = ChangePasswordRequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, new ChangePasswordRequest());
    }

    private static ProxyRequest makeProxyRequest(String body) {
        ProxyRequest request = new ProxyRequest();
        request.setHeaders(new HashMap<>(Collections.singletonMap(
            HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")));
        request.setBody(body);
        return request;
    }

}