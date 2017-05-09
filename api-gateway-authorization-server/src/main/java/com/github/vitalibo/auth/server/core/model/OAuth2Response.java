package com.github.vitalibo.auth.server.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuth2Response {

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "expires_in")
    private Long expiresIn;

    @JsonProperty(value = "token_type")
    private String tokenType;

}
