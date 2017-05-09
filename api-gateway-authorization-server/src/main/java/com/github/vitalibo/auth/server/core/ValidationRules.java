package com.github.vitalibo.auth.server.core;

import com.amazonaws.util.StringUtils;
import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.server.core.model.OAuth2Request;

public class ValidationRules {

    public static void verifyBody(ProxyRequest request, ErrorState errorState) {
        String body = request.getBody();

        if (StringUtils.isNullOrEmpty(body)) {
            errorState.addError(
                "body",
                "Required not empty body");
        }
    }

    public static void verifyBasicAuthenticationHeader(ProxyRequest request, ErrorState errorState) {
        String authorization = request.getHeaders().get("Authorization");
        if (StringUtils.isNullOrEmpty(authorization)) {
            return;
        }

        try {
            BasicAuthenticationHeader.decode(authorization);
        } catch (IllegalArgumentException e) {
            errorState.addError(
                "Authorization",
                "Basic Authentication header has incorrect format");
        }
    }

    public static void verifyGrantType(OAuth2Request request, ErrorState errorState) {
        String grantType = request.getGrantType();

        if (StringUtils.isNullOrEmpty(grantType)) {
            errorState.addError(
                "grant_type",
                "Required fields cannot be empty");
            return;
        }

        if (!"client_credentials".equals(grantType)) {
            errorState.addError(
                "grant_type",
                "The value is unknown. Acceptable value 'client_credentials'");
        }
    }

    public static void verifyClientId(OAuth2Request request, ErrorState errorState) {
        String clientId = request.getClientId();

        if (StringUtils.isNullOrEmpty(clientId)) {
            errorState.addError(
                "client_id",
                "Required fields cannot be empty");
        }
    }

    public static void verifyClientSecret(OAuth2Request request, ErrorState errorState) {
        String clientSecret = request.getClientSecret();

        if (StringUtils.isNullOrEmpty(clientSecret)) {
            errorState.addError(
                "client_secret",
                "Required fields cannot be empty");
        }
    }

}
