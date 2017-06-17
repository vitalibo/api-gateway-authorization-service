package com.github.vitalibo.authorization.server.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "previous_password")
    private String previousPassword;

    @JsonProperty(value = "proposed_password")
    private String proposedPassword;

}
