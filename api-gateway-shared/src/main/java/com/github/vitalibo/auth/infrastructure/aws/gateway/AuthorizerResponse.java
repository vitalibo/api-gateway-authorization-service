package com.github.vitalibo.auth.infrastructure.aws.gateway;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.util.json.Jackson;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizerResponse {

    private final String principalId;
    private final Map<?, ?> policyDocument;
    private final Map<String, ?> context;

    public static class Builder {

        private String principalId;
        private Policy policyDocument;
        private Map<String, Object> context = new LinkedHashMap<>();

        public Builder withPrincipalId(String principalId) {
            this.principalId = principalId;
            return this;
        }

        public Builder withPolicyDocument(Policy policyDocument) {
            this.policyDocument = policyDocument;
            return this;
        }

        public Builder withContextAsString(String key, String value) {
            return withContext(key, value);
        }

        public Builder withContextAsNumber(String key, Integer value) {
            return withContext(key, value);
        }

        public Builder withContextAsNumber(String key, Double value) {
            return withContext(key, value);
        }

        public Builder withContextAsBoolean(String key, Boolean value) {
            return withContext(key, value);
        }

        private Builder withContext(String key, Object value) {
            this.context.put(key, value);
            return this;
        }

        public AuthorizerResponse build() {
            return new AuthorizerResponse(
                principalId,
                Collections.unmodifiableMap(
                    Jackson.fromJsonString(policyDocument.toJson(), Map.class)),
                Collections.unmodifiableMap(context));
        }

    }

}
