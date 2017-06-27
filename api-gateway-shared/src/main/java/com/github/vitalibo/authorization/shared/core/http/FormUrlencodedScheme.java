package com.github.vitalibo.authorization.shared.core.http;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;
import lombok.SneakyThrows;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;

import java.util.Map;
import java.util.stream.Collectors;

public class FormUrlencodedScheme {

    private FormUrlencodedScheme() {
        super();
    }

    public static ProxyRequest decode(ProxyRequest request) {
        Map<String, String> headers = request.getHeaders();
        String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        if (contentType == null || !contentType.contains(URLEncodedUtils.CONTENT_TYPE)) {
            return request;
        }

        request.setBody(Jackson.toJsonString(
            decode(request.getBody(), headers)));
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        return request;
    }

    @SneakyThrows
    public static Map<String, String> decode(String body, Map<String, String> headers) {
        StringEntity entity = new StringEntity(body);
        entity.setContentType(headers.get(HttpHeaders.CONTENT_TYPE));

        return URLEncodedUtils.parse(entity).stream()
            .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
    }

}
