package com.github.vitalibo.authorization.server.core.translator;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.server.core.model.ChangePasswordRequest;
import com.github.vitalibo.authorization.shared.core.FormURLEncoded;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;

public class ChangePasswordRequestTranslator {

    public static ChangePasswordRequest from(ProxyRequest request) {
        final String body = FormURLEncoded.decode(
            request.getBody(), request.getHeaders());

        return Jackson.fromJsonString(
            StringUtils.isNullOrEmpty(body) ? "{}" : body,
            ChangePasswordRequest.class);
    }

}
