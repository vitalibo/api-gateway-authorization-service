package com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy;

import com.amazonaws.util.json.Jackson;
import lombok.Data;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Data
public class ProxyResponse {

    private final Boolean isBase64Encoded;
    private final Integer statusCode;
    private final Map<String, String> headers;
    private final String body;

    public static class Builder {

        private Boolean isBase64Encoded;
        private Integer statusCode;
        private Map<String, String> headers;
        private Object body;

        public Builder() {
            statusCode = HttpStatus.SC_OK;
            headers = new HashMap<>();
        }

        public Builder withIsBase64Encoded(Boolean isBase64Encoded) {
            this.isBase64Encoded = isBase64Encoded;
            return this;
        }

        public Builder withStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder withHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public Builder withBody(Object body) {
            this.body = body;
            return this;
        }

        public ProxyResponse build() {
            return new ProxyResponse(
                isBase64Encoded, statusCode, headers,
                body instanceof String ? (String) body : Jackson.toJsonString(body));
        }

    }

}
