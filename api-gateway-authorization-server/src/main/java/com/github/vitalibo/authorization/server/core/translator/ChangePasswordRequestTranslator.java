package com.github.vitalibo.authorization.server.core.translator;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.server.core.model.ChangePasswordRequest;
import com.github.vitalibo.authorization.shared.core.http.FormUrlencodedScheme;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;

public class ChangePasswordRequestTranslator {

    public static ChangePasswordRequest from(ProxyRequest proxyRequest) {
        ProxyRequest request = FormUrlencodedScheme.decode(proxyRequest);

        return Jackson.fromJsonString(
            StringUtils.isNullOrEmpty(request.getBody()) ? "{}" : request.getBody(),
            ChangePasswordRequest.class);
    }

}
