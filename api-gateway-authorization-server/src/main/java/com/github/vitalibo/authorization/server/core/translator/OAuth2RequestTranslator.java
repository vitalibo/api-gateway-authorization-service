package com.github.vitalibo.authorization.server.core.translator;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.server.core.BasicAuthenticationHeader;
import com.github.vitalibo.authorization.server.core.model.OAuth2Request;
import com.github.vitalibo.authorization.shared.core.FormURLEncoded;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;

public class OAuth2RequestTranslator {

    public static OAuth2Request from(ProxyRequest httpRequest) {
        final String body = FormURLEncoded.decode(
            httpRequest.getBody(), httpRequest.getHeaders());

        OAuth2Request request = Jackson.fromJsonString(
            StringUtils.isNullOrEmpty(body) ? "{}" : body,
            OAuth2Request.class);

        if (!StringUtils.isNullOrEmpty(request.getClientId()) &&
            !StringUtils.isNullOrEmpty(request.getClientSecret())) {

            return request;
        }

        try {
            BasicAuthenticationHeader authorization = BasicAuthenticationHeader.decode(
                httpRequest.getHeaders()
                    .get("Authorization"));

            request.setClientId(authorization.getUser());
            request.setClientSecret(authorization.getPassword());
        } catch (IllegalArgumentException ignored) {
        }

        return request;
    }

}
