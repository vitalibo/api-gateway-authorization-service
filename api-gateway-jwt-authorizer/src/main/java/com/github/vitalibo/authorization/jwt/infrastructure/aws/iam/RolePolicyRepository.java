package com.github.vitalibo.authorization.jwt.infrastructure.aws.iam;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyResult;
import com.github.vitalibo.authorization.jwt.core.Claims;
import com.github.vitalibo.authorization.jwt.core.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class RolePolicyRepository implements PolicyRepository {

    private static final Pattern ROLE_ARN_PATTERN = Pattern.compile(
        "arn:aws:iam::[0-9]+:role/(.+/)?(?<roleName>[-A-Za-z0-9+=,.@_]+)");

    private final AmazonIdentityManagement client;

    @Override
    public Policy getPolicy(Claims claims) {
        List<Policy> policies = claims.getRoles()
            .stream()
            .flatMap(RolePolicyRepository::retrieveRoleName)
            .map(this::getRolePolicy)
            .collect(Collectors.toList());

        return PolicyCollector.join(policies);
    }

    @SneakyThrows
    private Policy getRolePolicy(String roleName) {
        GetRolePolicyResult rolePolicyResult = client.getRolePolicy(
            new GetRolePolicyRequest()
                .withPolicyName("Inline")
                .withRoleName(roleName));

        return Policy.fromJson(URLDecoder.decode(
            rolePolicyResult.getPolicyDocument(), "UTF-8"));
    }

    private static Stream<String> retrieveRoleName(String arn) {
        Matcher matcher = ROLE_ARN_PATTERN.matcher(arn);
        if (matcher.matches()) {
            return Stream.of(matcher.group("roleName"));
        }

        return Stream.empty();
    }

}