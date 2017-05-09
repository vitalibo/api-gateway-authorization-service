package com.github.vitalibo.auth.server.core.translator;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.server.core.BasicAuthenticationHeader;
import com.github.vitalibo.auth.server.core.model.OAuth2Request;

public class OAuth2RequestTranslator {

    public static OAuth2Request from(ProxyRequest httpRequest) {
        OAuth2Request request = Jackson.fromJsonString(
            StringUtils.isNullOrEmpty(httpRequest.getBody()) ? "{}" : httpRequest.getBody(),
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
