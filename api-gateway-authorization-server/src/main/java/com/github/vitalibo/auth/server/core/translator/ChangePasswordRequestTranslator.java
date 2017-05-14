package com.github.vitalibo.auth.server.core.translator;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.server.core.model.ChangePasswordRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ChangePasswordRequestTranslator {

    public static ChangePasswordRequest from(ProxyRequest request) {
        try {
            String body = URLDecoder.decode(request.getBody(), "UTF-8");
            Map<String, String> map = Arrays.stream(body.split("&"))
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(o -> o[0], o -> o[1]));

            return Jackson.fromJsonString(Jackson.toJsonString(map),
                ChangePasswordRequest.class);
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException e) {
            return new ChangePasswordRequest();
        }
    }

}
