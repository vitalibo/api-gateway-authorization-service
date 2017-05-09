package com.github.vitalibo.auth.server.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuth2Request {

    @JsonProperty(value = "grant_type")
    private String grantType;

    @JsonProperty(value = "client_id")
    private String clientId;

    @JsonProperty(value = "client_secret")
    private String clientSecret;

}
