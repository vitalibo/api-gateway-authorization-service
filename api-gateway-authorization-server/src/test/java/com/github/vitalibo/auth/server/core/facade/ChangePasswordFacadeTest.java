package com.github.vitalibo.auth.server.core.facade;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.core.Principal;
import com.github.vitalibo.auth.core.Rule;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyResponse;
import com.github.vitalibo.auth.server.core.UserPool;
import com.github.vitalibo.auth.server.core.UserPoolException;
import com.github.vitalibo.auth.server.core.model.ChangePasswordRequest;
import com.github.vitalibo.auth.server.core.model.ChangePasswordResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.velocity.Template;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.Collection;

public class ChangePasswordFacadeTest {

    @Mock
    private UserPool mockUserPool;
    @Mock
    private Template mockTemplate;
    @Spy
    private ErrorState spyErrorState;
    @Mock
    private Collection<Rule<ProxyRequest>> mockPreRules;
    @Mock
    private Collection<Rule<ChangePasswordRequest>> mockPostRules;

    private ChangePasswordFacade facade;

    @BeforeMethod
    public void setUp() throws UserPoolException {
        MockitoAnnotations.initMocks(this);
        facade = new ChangePasswordFacade(
            spyErrorState, mockUserPool, mockTemplate, mockPreRules, mockPostRules);
        Mockito.when(mockUserPool.authenticate(Mockito.eq("admin"), Mockito.eq("foo")))
            .thenReturn(new Principal());
    }

    @Test
    public void testInvokeGetMethod() throws Exception {
        ProxyRequest request = new ProxyRequest();
        request.setHttpMethod("GET");
        Mockito.doAnswer(o -> {
            o.<StringWriter>getArgument(1).append("foo bar");
            return o;
        }).when(mockTemplate).merge(Mockito.any(), Mockito.any());

        ProxyResponse actual = facade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_OK);
        Assert.assertEquals(actual.getHeaders().get(HttpHeaders.CONTENT_TYPE), "text/html; charset=utf-8");
        Assert.assertEquals(actual.getBody(), "foo bar");
    }

    @Test
    public void testSuccessChangePassword() throws Exception {
        ProxyRequest request = makeProxyRequest();
        Mockito.when(mockUserPool.changePassword(Mockito.any(Principal.class), Mockito.eq("bar")))
            .thenReturn(true);

        ProxyResponse actual = facade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_OK);
        Assert.assertEquals(actual.getHeaders().get(HttpHeaders.CONTENT_TYPE), "application/json");
        ChangePasswordResponse response = Jackson.fromJsonString(actual.getBody(), ChangePasswordResponse.class);
        Assert.assertTrue(response.getAcknowledged());
        Assert.assertTrue(response.getMessage().contains("successfully"));
    }

    @Test
    public void testFailChangePassword() throws Exception {
        ProxyRequest request = makeProxyRequest();
        Mockito.when(mockUserPool.changePassword(Mockito.any(Principal.class), Mockito.eq("bar")))
            .thenThrow(new UserPoolException("foo"));

        ProxyResponse actual = facade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_OK);
        Assert.assertEquals(actual.getHeaders().get(HttpHeaders.CONTENT_TYPE), "application/json");
        ChangePasswordResponse response = Jackson.fromJsonString(actual.getBody(), ChangePasswordResponse.class);
        Assert.assertFalse(response.getAcknowledged());
        Assert.assertFalse(response.getMessage().contains("successfully"));
    }

    private static ProxyRequest makeProxyRequest() {
        ProxyRequest request = new ProxyRequest();
        request.setHttpMethod("POST");
        request.setBody("username=admin&previous_password=foo&proposed_password=bar");
        return request;
    }

}