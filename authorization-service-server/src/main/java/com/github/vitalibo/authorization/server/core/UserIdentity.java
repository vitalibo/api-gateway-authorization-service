package com.github.vitalibo.authorization.server.core;

import lombok.Data;

@Data
public class UserIdentity {

    private String username;
    private String session;
    private String accessToken;

}
