package com.github.vitalibo.authorization.basic.infrastructure.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class AmazonFactory {

    private final Regions region;
    private final AWSCredentialsProvider credentials;

    @Getter(lazy = true)
    private final AWSCognitoIdentityProvider awsCognitoIdentityProvider =
        AWSCognitoIdentityProviderClient.builder()
            .withRegion(region)
            .withCredentials(credentials)
            .build();

    AmazonFactory(String region) {
        this(Regions.fromName(region), new DefaultAWSCredentialsProviderChain());
    }

}
