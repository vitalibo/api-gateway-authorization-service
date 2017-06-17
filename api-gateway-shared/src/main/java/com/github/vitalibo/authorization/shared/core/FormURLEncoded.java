package com.github.vitalibo.authorization.shared.core;

import com.amazonaws.util.json.Jackson;
import lombok.SneakyThrows;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;

import java.util.Map;
import java.util.stream.Collectors;

public class FormURLEncoded {

    @SneakyThrows
    public static String decode(String body, Map<String, String> headers) {
        final String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        if (contentType == null || !contentType.contains(URLEncodedUtils.CONTENT_TYPE)) {

            return body;
        }

        StringEntity entity = new StringEntity(body);
        entity.setContentType(contentType);

        return Jackson.toJsonString(
            URLEncodedUtils.parse(entity).stream()
                .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue)));
    }

}
