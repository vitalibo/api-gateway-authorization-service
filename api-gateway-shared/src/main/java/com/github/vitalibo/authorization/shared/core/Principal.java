package com.github.vitalibo.authorization.shared.core;

import lombok.Data;

@Data
public class Principal {

    private String id;
    private String username;
    private String session;
    private String accessToken;

}
