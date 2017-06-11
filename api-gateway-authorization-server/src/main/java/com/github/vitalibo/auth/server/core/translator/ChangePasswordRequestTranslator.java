package com.github.vitalibo.auth.server.core.translator;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.core.FormURLEncoded;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.server.core.model.ChangePasswordRequest;

public class ChangePasswordRequestTranslator {

    public static ChangePasswordRequest from(ProxyRequest request) {
        final String body = FormURLEncoded.decode(
            request.getBody(), request.getHeaders());

        return Jackson.fromJsonString(
            StringUtils.isNullOrEmpty(body) ? "{}" : body,
            ChangePasswordRequest.class);
    }

}
