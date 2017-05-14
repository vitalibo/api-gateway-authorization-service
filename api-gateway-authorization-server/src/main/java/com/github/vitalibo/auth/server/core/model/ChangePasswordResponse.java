package com.github.vitalibo.auth.server.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangePasswordResponse {

    @JsonProperty(value = "acknowledged")
    private Boolean acknowledged;

    @JsonProperty(value = "message")
    private String message;

}
