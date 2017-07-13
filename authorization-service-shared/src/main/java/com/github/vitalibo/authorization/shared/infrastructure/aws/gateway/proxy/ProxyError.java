package com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.vitalibo.authorization.shared.core.validation.ErrorState;
import lombok.Data;
import org.apache.http.impl.EnglishReasonPhraseCatalog;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

@Data
@JsonInclude(Include.NON_NULL)
public class ProxyError {

    @JsonProperty(value = "status")
    private final Integer status;

    @JsonProperty(value = "message")
    private final String message;

    @JsonProperty(value = "errors")
    private final Map<String, Collection<String>> errors;

    @JsonProperty(value = "request-id")
    private final String requestId;

    public static class Builder {

        private static final EnglishReasonPhraseCatalog HTTP_STATUS_PHRASE =
            EnglishReasonPhraseCatalog.INSTANCE;

        private Integer statusCode;
        private ErrorState errorState;
        private String requestId;

        public Builder withStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withErrorState(ErrorState errorState) {
            this.errorState = errorState;
            return this;
        }

        public Builder withRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public ProxyResponse build() {
            if (statusCode == null) {
                throw new IllegalArgumentException("Status code can't be Null");
            }

            return new ProxyResponse.Builder()
                .withStatusCode(statusCode)
                .withBody(new ProxyError(
                    statusCode,
                    HTTP_STATUS_PHRASE.getReason(
                        statusCode, Locale.ENGLISH),
                    errorState,
                    requestId))
                .build();
        }

    }

}
