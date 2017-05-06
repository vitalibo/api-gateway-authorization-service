package com.github.vitalibo.auth.infrastructure.aws.gateway.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class ProxyRequestTranslator {

    public static ProxyRequest ofNullable(ProxyRequest o) {
        ProxyRequest r = new ProxyRequest();
        r.setResource(notNull(o.getResource()));
        r.setPath(notNull(o.getPath()));
        r.setHttpMethod(notNull(o.getHttpMethod()));
        r.setHeaders(notNull(o.getHeaders()));
        r.setQueryStringParameters(notNull(o.getQueryStringParameters()));
        r.setPathParameters(notNull(o.getPathParameters()));
        r.setStageVariables(notNull(o.getStageVariables()));
        r.setRequestContext(notNull(o.getRequestContext()));
        r.setBody(notNull(o.getBody()));
        r.setIsBase64Encoded(notNull(o.getIsBase64Encoded()));
        return r;
    }

    private static String notNull(String o) {
        return Optional.ofNullable(o).orElse("");
    }

    private static <T> Map<String, T> notNull(Map<String, T> o) {
        return Optional.ofNullable(o)
            .map(ProxyRequestTranslator::caseInsensitiveMap)
            .orElse(new HashMap<>());
    }

    private static Boolean notNull(Boolean o) {
        return Optional.ofNullable(o).orElse(false);
    }

    private static <T> Map<String, T> caseInsensitiveMap(Map<String, T> o) {
        Map<String, T> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(o);
        return map;
    }

}
