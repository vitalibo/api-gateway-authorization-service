package com.github.vitalibo.auth.jwt.infrastructure.aws.iam;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Statement;

import java.util.Collection;
import java.util.List;

class PolicyCollector {

    static Policy join(List<Policy> policies) {
        return new Policy()
            .withStatements(policies.stream()
                .map(Policy::getStatements)
                .flatMap(Collection::stream)
                .toArray(Statement[]::new));
    }

}
