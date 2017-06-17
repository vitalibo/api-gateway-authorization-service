package com.github.vitalibo.authorization.server.core.translator;

import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.authorization.server.TestHelper;
import com.github.vitalibo.authorization.server.core.model.OAuth2Request;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class OAuth2RequestTranslatorTest {

    @Test
    public void testTranslateBody() {
        ProxyRequest request = new ProxyRequest();
        request.setBody(TestHelper.resourceAsString("/OAuth2Request.json"));
        request.setHeaders(Collections.emptyMap());

        OAuth2Request actual = OAuth2RequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getGrantType(), "client_credentials");
        Assert.assertEquals(actual.getClientId(), "1234567890");
        Assert.assertEquals(actual.getClientSecret(), "zaq1xsw2cde3vfr4bgt5nhy6");
    }

    @Test
    public void testTranslateHeader() {
        ProxyRequest request = new ProxyRequest();
        request.setBody("{\"grant_type\":\"client_credentials\"}");
        request.setHeaders(Collections.singletonMap(
            "Authorization", "Basic MTIzNDU2Nzg5MDp6YXExeHN3MmNkZTN2ZnI0Ymd0NW5oeTY="));

        OAuth2Request actual = OAuth2RequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getGrantType(), "client_credentials");
        Assert.assertEquals(actual.getClientId(), "1234567890");
        Assert.assertEquals(actual.getClientSecret(), "zaq1xsw2cde3vfr4bgt5nhy6");
    }

    @Test
    public void testEmpty() {
        ProxyRequest request = new ProxyRequest();
        request.setBody("{}");
        request.setHeaders(Collections.emptyMap());

        OAuth2Request actual = OAuth2RequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertNull(actual.getGrantType());
        Assert.assertNull(actual.getClientId());
        Assert.assertNull(actual.getClientSecret());
    }

}