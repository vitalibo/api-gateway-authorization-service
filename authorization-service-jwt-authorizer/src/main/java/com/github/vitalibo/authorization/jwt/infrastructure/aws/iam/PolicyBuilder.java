package com.github.vitalibo.authorization.jwt.infrastructure.aws.iam;

import com.amazonaws.auth.policy.Condition;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class PolicyBuilder {

    @Getter
    private final List<Statement> statements;

    PolicyBuilder() {
        statements = new ArrayList<>();
    }

    PolicyBuilder withPolicies(List<Policy> policies) {
        statements.addAll(
            policies.stream()
                .map(Policy::getStatements)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        return this;
    }

    PolicyBuilder withExpiredAt(ZonedDateTime expiredAt) {
        Statement expiredAtStatement = new Statement(Statement.Effect.Deny)
            .withActions(() -> "*")
            .withResources(new Resource("*"))
            .withConditions(new Condition()
                .withType("DateGreaterThan")
                .withConditionKey("aws:CurrentTime")
                .withValues(expiredAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        statements.add(expiredAtStatement);
        return this;
    }

    Policy build() {
        return new Policy()
            .withStatements(statements.toArray(new Statement[0]));
    }

}
