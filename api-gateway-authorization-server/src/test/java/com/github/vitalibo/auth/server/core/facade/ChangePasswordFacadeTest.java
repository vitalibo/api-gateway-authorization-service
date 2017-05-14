package com.github.vitalibo.auth.server.core.facade;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.core.Rule;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyResponse;
import com.github.vitalibo.auth.server.core.UserPool;
import com.github.vitalibo.auth.server.core.model.ChangePasswordRequest;
import com.github.vitalibo.auth.server.core.model.ChangePasswordResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
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
    private VelocityEngine mockVelocityEngine;
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
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockVelocityEngine.getTemplate(Mockito.any()))
            .thenReturn(mockTemplate);
        facade = new ChangePasswordFacade(
            mockUserPool, mockVelocityEngine,
            spyErrorState, mockPreRules, mockPostRules);
    }

    @Test
    public void testInvokeGetMethod() {
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
    public void testSuccessChangePassword() {
        ProxyRequest request = new ProxyRequest();
        request.setHttpMethod("POST");
        request.setBody("username=admin");
        Mockito.when(mockUserPool.changePassword(Mockito.eq("admin"), Mockito.any(), Mockito.any()))
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
    public void testFailChangePassword() {
        ProxyRequest request = new ProxyRequest();
        request.setHttpMethod("POST");
        request.setBody("username=admin");
        Mockito.when(mockUserPool.changePassword(Mockito.eq("admin"), Mockito.any(), Mockito.any()))
            .thenReturn(false);

        ProxyResponse actual = facade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) HttpStatus.SC_OK);
        Assert.assertEquals(actual.getHeaders().get(HttpHeaders.CONTENT_TYPE), "application/json");
        ChangePasswordResponse response = Jackson.fromJsonString(actual.getBody(), ChangePasswordResponse.class);
        Assert.assertFalse(response.getAcknowledged());
        Assert.assertFalse(response.getMessage().contains("successfully"));
    }

}