package com.github.vitalibo.authorization.server.core;

import com.amazonaws.util.StringUtils;
import com.github.vitalibo.authorization.server.core.model.ChangePasswordRequest;
import com.github.vitalibo.authorization.server.core.model.OAuth2Request;
import com.github.vitalibo.authorization.shared.core.http.BasicAuthenticationException;
import com.github.vitalibo.authorization.shared.core.http.BasicScheme;
import com.github.vitalibo.authorization.shared.core.validation.ErrorState;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;

public final class ValidationRules {

    private ValidationRules() {
    }

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
            BasicScheme.decode(request.getHeaders());
        } catch (BasicAuthenticationException e) {
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

    public static void verifyUserName(ChangePasswordRequest request, ErrorState errorState) {
        String username = request.getUsername();

        if (StringUtils.isNullOrEmpty(username)) {
            errorState.addError(
                "username",
                "Required fields cannot be empty");
        }
    }

    public static void verifyPreviousPassword(ChangePasswordRequest request, ErrorState errorState) {
        String previousPassword = request.getPreviousPassword();

        if (StringUtils.isNullOrEmpty(previousPassword)) {
            errorState.addError(
                "previous_password",
                "Required fields cannot be empty");
        }
    }

    public static void verifyProposedPassword(ChangePasswordRequest request, ErrorState errorState) {
        String proposedPassword = request.getProposedPassword();

        if (StringUtils.isNullOrEmpty(proposedPassword)) {
            errorState.addError(
                "proposed_password",
                "Required fields cannot be empty");
        }
    }

}
