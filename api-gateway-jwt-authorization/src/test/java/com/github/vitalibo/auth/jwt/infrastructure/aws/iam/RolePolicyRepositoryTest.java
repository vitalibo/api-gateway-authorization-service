package com.github.vitalibo.auth.jwt.infrastructure.aws.iam;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyResult;
import com.github.vitalibo.auth.jwt.TestHelper;
import com.github.vitalibo.auth.jwt.core.Claims;
import com.github.vitalibo.auth.jwt.core.PolicyRepository;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RolePolicyRepositoryTest {

    @Mock
    private AmazonIdentityManagement mockAmazonIdentityManagement;
    @Mock
    private GetRolePolicyResult mockGetRolePolicyResult;
    @Captor
    private ArgumentCaptor<GetRolePolicyRequest> getRolePolicyRequestCaptor;

    private PolicyRepository repository;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        repository = new RolePolicyRepository(mockAmazonIdentityManagement);
    }

    @Test
    public void testGetPolicy() {
        Claims claims = new Claims();
        claims.setUsername("foo");
        claims.setRoles(Arrays.asList(
            "arn:aws:iam::1234567890:role/foo-role",
            "arn:aws:iam::0987654321:role/bar-role"));

        Mockito.when(mockAmazonIdentityManagement.getRolePolicy(Mockito.any()))
            .thenReturn(mockGetRolePolicyResult);
        Mockito.when(mockGetRolePolicyResult.getPolicyDocument())
            .thenReturn(TestHelper.resourceAsString("/iam/PolicyDocument1"))
            .thenReturn(TestHelper.resourceAsString("/iam/PolicyDocument2"));

        Policy actual = repository.getPolicy(claims);

        Assert.assertNotNull(actual);
        Mockito.verify(mockAmazonIdentityManagement, Mockito.times(2))
            .getRolePolicy(getRolePolicyRequestCaptor.capture());
        List<GetRolePolicyRequest> requests = getRolePolicyRequestCaptor.getAllValues();
        Assert.assertEquals(
            requests.stream().map(GetRolePolicyRequest::getRoleName).collect(Collectors.toList()),
            Arrays.asList("foo-role", "bar-role"));
        Assert.assertEquals(actual.toJson(), TestHelper.resourceAsJsonString("/Policy.json"));
    }

    @Test
    public void testGetUserPolicy() {
        Claims claims = new Claims();
        claims.setUsername("foo");
        claims.setRoles(Collections.singletonList(
            "arn:aws:iam::1234567890:user/foo-user"));

        Policy policy = repository.getPolicy(claims);

        Assert.assertNotNull(policy);
        Assert.assertTrue(policy.getStatements().isEmpty());
        Mockito.verify(mockAmazonIdentityManagement, Mockito.never()).getRolePolicy(Mockito.any());
    }

}