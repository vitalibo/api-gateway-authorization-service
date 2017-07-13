package com.github.vitalibo.authorization.server.core.translator;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.server.core.model.ClientCredentialsRequest;
import com.github.vitalibo.authorization.shared.core.http.BasicAuthenticationException;
import com.github.vitalibo.authorization.shared.core.http.BasicScheme;
import com.github.vitalibo.authorization.shared.core.http.Credentials;
import com.github.vitalibo.authorization.shared.core.http.FormUrlencodedScheme;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;

public class ClientCredentialsRequestTranslator {

    public static ClientCredentialsRequest from(ProxyRequest o) {
        ProxyRequest proxyRequest = FormUrlencodedScheme.decode(o);

        ClientCredentialsRequest request = Jackson.fromJsonString(
            StringUtils.isNullOrEmpty(proxyRequest.getBody()) ? "{}" : proxyRequest.getBody(),
            ClientCredentialsRequest.class);

        if (!StringUtils.isNullOrEmpty(request.getClientId()) &&
            !StringUtils.isNullOrEmpty(request.getClientSecret())) {

            return request;
        }

        try {
            Credentials credentials = BasicScheme.decode(proxyRequest.getHeaders());

            request.setClientId(credentials.getUsername());
            request.setClientSecret(credentials.getPassword());
        } catch (BasicAuthenticationException ignored) {
        }

        return request;
    }

}
