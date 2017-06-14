package com.github.vitalibo.auth.jwt.infrastructure.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.github.vitalibo.auth.jwt.core.Jwt;
import com.github.vitalibo.auth.jwt.core.PolicyRepository;
import com.github.vitalibo.auth.jwt.infrastructure.aws.iam.RolePolicyRepository;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.Map;

public class Factory {

    private static final String AWS_REGION = "AWS_REGION";
    private static final String AWS_COGNITO_USER_POOL_ID = "AWS_COGNITO_USER_POOL_ID";

    @Getter(lazy = true)
    private static final Factory instance = new Factory(System.getenv());

    private final Regions awsRegion;
    private final JWKSource<? extends SecurityContext> jwkSource;

    @SneakyThrows
    Factory(Map<String, String> env) {
        awsRegion = Regions.fromName(env.get(AWS_REGION));
        jwkSource = new RemoteJWKSet<>(
            new URL(String.format(
                "https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json",
                awsRegion.getName(), env.get(AWS_COGNITO_USER_POOL_ID))));
    }

    public Jwt createJsonWebToken() {
        return new Jwt(jwkSource);
    }

    public PolicyRepository createRolePolicyRepository() {
        return new RolePolicyRepository(
            AmazonIdentityManagementClientBuilder.standard()
                .withRegion(awsRegion)
                .build());
    }

}
